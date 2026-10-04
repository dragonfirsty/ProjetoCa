package Model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericDAO {
    private static final String URL = "jdbc:sqlite:banco_rpg.db";

    public void inserirObjeto(Object objeto) {
        Class<?> tipo = objeto.getClass();
        List<Field> campos = camposPersistiveis(tipo);
        List<Field> inseriveis = new ArrayList<Field>();
        for (Field campo : campos) if (!ehId(campo) || !idAutoIncremental(campo)) inseriveis.add(campo);
        if (inseriveis.isEmpty()) {
            executar("INSERT INTO " + nomeTabela(tipo) + " DEFAULT VALUES", new Object[0]);
            return;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(nomeTabela(tipo)).append(" (");
        adicionarNomes(sql, inseriveis);
        sql.append(") VALUES (");
        adicionarParametros(sql, inseriveis.size());
        sql.append(")");
        executar(sql.toString(), valores(objeto, inseriveis));
    }

    public <T> int atualizarCampo(Class<T> tipo, Object id, String nomeCampo, Object valor) {
        Field campo = campoPorNome(tipo, nomeCampo);
        Field campoId = campoId(tipo);
        String sql = "UPDATE " + nomeTabela(tipo) + " SET " + nomeColuna(campo) + " = ? WHERE "
                + nomeColuna(campoId) + " = ?";
        return executar(sql, new Object[] { valor, id });
    }

    public <T> int removerObjeto(Class<T> tipo, Object id) {
        String sql = "DELETE FROM " + nomeTabela(tipo) + " WHERE " + nomeColuna(campoId(tipo)) + " = ?";
        return executar(sql, new Object[] { id });
    }

    public void removerObjeto(Object objeto) {
        Field id = campoId(objeto.getClass());
        removerObjeto(objeto.getClass(), valor(objeto, id));
    }

    public <T> List<T> selecionarTodosObjeto(Class<T> tipo) {
        String sql = "SELECT * FROM " + nomeTabela(tipo);
        List<T> resultado = new ArrayList<T>();
        try (Connection conexao = conectar(); PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet dados = comando.executeQuery()) {
            while (dados.next()) resultado.add(mapear(tipo, dados));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao selecionar registros", e);
        }
    }

    public void selecionarTodosObjeto(Object objeto) {
        imprimirRegistros("SELECT * FROM " + nomeTabela(objeto.getClass()), "Lendo a tabela");
    }

    public <T> T selecionarUmObjeto(Class<T> tipo, Object id) {
        String sql = "SELECT * FROM " + nomeTabela(tipo) + " WHERE " + nomeColuna(campoId(tipo)) + " = ?";
        try (Connection conexao = conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setObject(1, id);
            try (ResultSet dados = comando.executeQuery()) {
                return dados.next() ? mapear(tipo, dados) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao selecionar registro", e);
        }
    }

    public void selecionarUmObjeto(Object objeto) {
        Field id = campoId(objeto.getClass());
        imprimirRegistros("SELECT * FROM " + nomeTabela(objeto.getClass()) + " WHERE " + nomeColuna(id) + " = ?",
                "Registro encontrado", valor(objeto, id));
    }

    public void atualizarObjeto(Object objeto) {
        Class<?> tipo = objeto.getClass();
        Field id = campoId(tipo);
        List<Field> atualizaveis = new ArrayList<Field>();
        for (Field campo : camposPersistiveis(tipo)) if (!ehId(campo)) atualizaveis.add(campo);
        if (atualizaveis.isEmpty()) return;
        StringBuilder sql = new StringBuilder("UPDATE ").append(nomeTabela(tipo)).append(" SET ");
        for (int i = 0; i < atualizaveis.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(nomeColuna(atualizaveis.get(i))).append(" = ?");
        }
        sql.append(" WHERE ").append(nomeColuna(id)).append(" = ?");
        Object[] parametros = new Object[atualizaveis.size() + 1];
        for (int i = 0; i < atualizaveis.size(); i++) parametros[i] = valor(objeto, atualizaveis.get(i));
        parametros[parametros.length - 1] = valor(objeto, id);
        executar(sql.toString(), parametros);
    }

    public void criarTabela(Object objeto) {
        Class<?> tipo = objeto.getClass();
        List<Field> campos = camposPersistiveis(tipo);
        if (campos.isEmpty()) throw new IllegalArgumentException("A classe nao possui campos persistiveis");
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(nomeTabela(tipo)).append(" (");
        for (int i = 0; i < campos.size(); i++) {
            if (i > 0) sql.append(", ");
            Field campo = campos.get(i);
            Column coluna = campo.getAnnotation(Column.class);
            sql.append(nomeColuna(campo)).append(" ").append(tipoSql(campo, coluna));
            if (ehId(campo)) sql.append(" PRIMARY KEY");
            if (coluna != null && !coluna.nullable()) sql.append(" NOT NULL");
            if (coluna != null && coluna.unique()) sql.append(" UNIQUE");
            if (ehId(campo) && idAutoIncremental(campo) && tipoInteiro(campo.getType())) sql.append(" AUTOINCREMENT");
        }
        sql.append(")");
        executar(sql.toString(), new Object[0]);
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private int executar(String sql, Object[] parametros) {
        try (Connection conexao = conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) comando.setObject(i + 1, parametros[i]);
            return comando.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao executar SQL: " + sql, e);
        }
    }

    private List<Field> camposPersistiveis(Class<?> tipo) {
        List<Field> campos = new ArrayList<Field>();
        for (Class<?> atual = tipo; atual != null && atual != Object.class; atual = atual.getSuperclass()) {
            for (Field campo : atual.getDeclaredFields()) {
                if (!Modifier.isStatic(campo.getModifiers()) && !campo.isSynthetic()
                        && campo.getAnnotation(Transient.class) == null && tipoSqlSuportado(campo.getType())) campos.add(campo);
            }
        }
        return campos;
    }

    private String nomeTabela(Class<?> tipo) {
        Table tabela = tipo.getAnnotation(Table.class);
        return identificador(tabela != null && !tabela.name().isEmpty() ? tabela.name() : tipo.getSimpleName().toLowerCase());
    }

    private String nomeColuna(Field campo) {
        Column coluna = campo.getAnnotation(Column.class);
        return identificador(coluna != null && !coluna.name().isEmpty() ? coluna.name() : campo.getName());
    }

    private String identificador(String valor) {
        if (!valor.matches("[A-Za-z_][A-Za-z0-9_]*")) throw new IllegalArgumentException("Identificador SQL invalido: " + valor);
        return valor;
    }

    private Field campoId(Class<?> tipo) {
        for (Field campo : camposPersistiveis(tipo)) if (ehId(campo)) return campo;
        throw new IllegalArgumentException("A classe " + tipo.getName() + " nao possui campo identificador");
    }

    private Field campoPorNome(Class<?> tipo, String nome) {
        for (Field campo : camposPersistiveis(tipo)) if (campo.getName().equals(nome) || nomeColuna(campo).equals(nome)) return campo;
        throw new IllegalArgumentException("Campo nao encontrado: " + nome);
    }

    private boolean ehId(Field campo) {
        return campo.getAnnotation(Id.class) != null || campo.getName().equalsIgnoreCase("id");
    }

    private boolean idAutoIncremental(Field campo) {
        Id id = campo.getAnnotation(Id.class);
        return id == null || id.autoIncrement();
    }

    private String tipoSql(Field campo, Column coluna) {
        if (coluna != null && !coluna.sqlType().isEmpty()) return coluna.sqlType();
        Class<?> tipo = campo.getType();
        if (tipo == String.class) return "VARCHAR(" + (coluna == null ? 255 : coluna.length()) + ")";
        if (tipo == int.class || tipo == Integer.class || tipo == short.class || tipo == Short.class) return "INTEGER";
        if (tipo == long.class || tipo == Long.class) return "BIGINT";
        if (tipo == double.class || tipo == Double.class) return "DOUBLE";
        if (tipo == float.class || tipo == Float.class) return "FLOAT";
        if (tipo == boolean.class || tipo == Boolean.class) return "BOOLEAN";
        if (tipo == LocalDate.class) return "DATE";
        if (tipo == LocalDateTime.class) return "TIMESTAMP";
        throw new IllegalArgumentException("Tipo nao suportado: " + tipo.getName());
    }

    private boolean tipoSqlSuportado(Class<?> tipo) {
        return tipo == String.class || tipo == int.class || tipo == Integer.class || tipo == short.class || tipo == Short.class
                || tipo == long.class || tipo == Long.class || tipo == double.class || tipo == Double.class
                || tipo == float.class || tipo == Float.class || tipo == boolean.class || tipo == Boolean.class
                || tipo == LocalDate.class || tipo == LocalDateTime.class;
    }

    private boolean tipoInteiro(Class<?> tipo) {
        return tipo == int.class || tipo == Integer.class || tipo == long.class || tipo == Long.class;
    }

    private Object[] valores(Object objeto, List<Field> campos) {
        Object[] valores = new Object[campos.size()];
        for (int i = 0; i < campos.size(); i++) valores[i] = valor(objeto, campos.get(i));
        return valores;
    }

    private Object valor(Object objeto, Field campo) {
        try {
            campo.setAccessible(true);
            Object valor = campo.get(objeto);
            if (valor instanceof LocalDate) return java.sql.Date.valueOf((LocalDate) valor);
            if (valor instanceof LocalDateTime) return Timestamp.valueOf((LocalDateTime) valor);
            return valor;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Nao foi possivel acessar o campo " + campo.getName(), e);
        }
    }

    private <T> T mapear(Class<T> tipo, ResultSet dados) throws SQLException {
        try {
            Constructor<T> construtor = tipo.getDeclaredConstructor();
            construtor.setAccessible(true);
            T objeto = construtor.newInstance();
            Map<String, String> colunas = new HashMap<String, String>();
            ResultSetMetaData meta = dados.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) colunas.put(meta.getColumnName(i).toLowerCase(), meta.getColumnName(i));
            for (Field campo : camposPersistiveis(tipo)) {
                String coluna = colunas.get(nomeColuna(campo).toLowerCase());
                if (coluna != null) {
                    campo.setAccessible(true);
                    campo.set(objeto, converter(dados.getObject(coluna), campo.getType()));
                }
            }
            return objeto;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("A classe " + tipo.getName() + " precisa de construtor sem argumentos", e);
        }
    }

    private Object converter(Object valor, Class<?> tipo) {
        if (valor == null) return null;
        if (tipo == LocalDate.class && valor instanceof java.sql.Date) return ((java.sql.Date) valor).toLocalDate();
        if (tipo == LocalDateTime.class && valor instanceof Timestamp) return ((Timestamp) valor).toLocalDateTime();
        if ((tipo == boolean.class || tipo == Boolean.class) && valor instanceof Number) return ((Number) valor).intValue() != 0;
        return valor;
    }

    private void adicionarNomes(StringBuilder sql, List<Field> campos) {
        for (int i = 0; i < campos.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(nomeColuna(campos.get(i)));
        }
    }

    private void adicionarParametros(StringBuilder sql, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
    }

    private void imprimirRegistros(String sql, String titulo, Object... parametros) {
        try (Connection conexao = conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) comando.setObject(i + 1, parametros[i]);
            try (ResultSet dados = comando.executeQuery()) {
                ResultSetMetaData meta = dados.getMetaData();
                while (dados.next()) {
                    StringBuilder linha = new StringBuilder(titulo).append(" -> ");
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        if (i > 1) linha.append(" | ");
                        linha.append(meta.getColumnName(i)).append(": ").append(dados.getObject(i));
                    }
                    System.out.println(linha);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar registros", e);
        }
    }
}