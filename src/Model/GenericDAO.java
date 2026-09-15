package Model;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericDAO {

    // Método para buscar os atributos da classe filha e das classes pai
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public void inserirObjeto(Object o) {
        Class<?> clazz = o.getClass();
        String nomeClasse = clazz.getSimpleName().toLowerCase();

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(nomeClasse).append(" (");

        List<Field> campos = getAllFields(clazz);

        // laço de colunas
        for (int i = 0; i < campos.size(); i++) {
            // pula valores que não devem entrar
            if (campos.get(i).getName().equals("itens") || campos.get(i).getName().equals("mirando") ||  campos.get(i).getName().equals("id")) continue;
            
            sql.append(campos.get(i).getName());
            if (i < campos.size() - 1) sql.append(", ");
        }

        //tira virgula caso o valor tenha pulado
        if (sql.toString().endsWith(", ")) sql.setLength(sql.length() - 2);
        
        sql.append(") VALUES (");

        // laço dos valores para as colunas
        for (int i = 0; i < campos.size(); i++) {
            Field campo = campos.get(i);
            if (campo.getName().equals("itens") || campo.getName().equals("mirando") || campo.getName().equals("id")) continue;

            campo.setAccessible(true);
            try {
                Object valor = campo.get(o);
                if (valor instanceof String) {
                    sql.append("'").append(valor).append("'");
                } else {
                    sql.append(valor);
                }
                if (i < campos.size() - 1) sql.append(", ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        if (sql.toString().endsWith(", ")) sql.setLength(sql.length() - 2);
        sql.append(");");

        executarComando(sql.toString());
    }
    
    public void selecionarTodosObjeto(Object o) {
    	Class<?> clazz = o.getClass();
        String nomeClasse = clazz.getSimpleName().toLowerCase();
        String sql = "SELECT * FROM " + nomeClasse;
        
        System.out.println("\n--- Lendo a tabela: " + nomeClasse.toUpperCase() + " ---");
        
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:banco_rpg.db");
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql)) {
               
               // O MetaData descobre dinamicamente quais são as colunas da tabela
               java.sql.ResultSetMetaData metaData = rs.getMetaData();
               int numeroDeColunas = metaData.getColumnCount();
               
               // Laço para ler cada linha do banco de dados
               while (rs.next()) {
                   StringBuilder linha = new StringBuilder("Registro do Banco -> ");
                   
                   // Laço para ler cada coluna daquela linha
                   for (int i = 1; i <= numeroDeColunas; i++) {
                       linha.append(metaData.getColumnName(i)).append(": ").append(rs.getString(i));
                       if (i < numeroDeColunas) linha.append(" | ");
                   }
                   System.out.println(linha.toString());
               }
               
           } catch (Exception e) {
               System.out.println("Erro na consulta: " + e.getMessage());
           }
    }
    public void selecionarUmObjeto(Object o) {
        Class<?> clazz = o.getClass();
        String nomeClasse = clazz.getSimpleName().toLowerCase();
        List<Field> campos = getAllFields(clazz);
        Object idValor = null;
        
        for (Field campo : campos) {
            if (campo.getName().equals("id")) {
                campo.setAccessible(true);
                try {
                    idValor = campo.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        String sql = "SELECT * FROM " + nomeClasse + " WHERE id = " + idValor + ";";
        System.out.println("\n--- Buscando " + nomeClasse.toUpperCase() + " com ID: " + idValor + " ---");
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:banco_rpg.db");
             Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int numeroDeColunas = metaData.getColumnCount();
            
            if (rs.next()) {
                StringBuilder linha = new StringBuilder("Encontrado -> ");
                for (int i = 1; i <= numeroDeColunas; i++) {
                    linha.append(metaData.getColumnName(i)).append(": ").append(rs.getString(i));
                    if (i < numeroDeColunas) linha.append(" | ");
                }
                System.out.println(linha.toString());
            } else {
                System.out.println("Nenhum registro encontrado com o ID " + idValor);
            }
            
        } catch (Exception e) {
            System.out.println("Erro na consulta: " + e.getMessage());
        }
    }
    
    public void atualizarObjeto(Object o) {
        Class<?> clazz = o.getClass();
        String nomeClasse = clazz.getSimpleName().toLowerCase();
        List<Field> campos = getAllFields(clazz);
        
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(nomeClasse).append(" SET ");
        
        Object idValor = null;

        for (int i = 0; i < campos.size(); i++) {
            Field campo = campos.get(i);
            String nomeCampo = campo.getName();
            
            campo.setAccessible(true);
            try {
                Object valor = campo.get(o);
                
                if (nomeCampo.equals("id")) {
                    idValor = valor;
                    continue; 
                }
                if (nomeCampo.equals("itens") || nomeCampo.equals("mirando")) {
                    continue;
                }
                sql.append(nomeCampo).append(" = ");
                if (valor instanceof String) {
                    sql.append("'").append(valor).append("'");
                } else {
                    sql.append(valor);
                }
                sql.append(", ");
                
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
     
        if (sql.toString().endsWith(", ")) {
            sql.setLength(sql.length() - 2);
        }
        
       
        sql.append(" WHERE id = ").append(idValor).append(";");
        
        executarComando(sql.toString());
    }
    
    public void removerObjeto(Object o) {
        Class<?> clazz = o.getClass();
        String nomeClasse = clazz.getSimpleName().toLowerCase();
        List<Field> campos = getAllFields(clazz);
        Object idValor = null;

        for (Field campo : campos) {
            if (campo.getName().equals("id")) {
                campo.setAccessible(true);
                try {
                    idValor = campo.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        String sql = "DELETE FROM " + nomeClasse + " WHERE id = " + idValor + ";";
        executarComando(sql);
    }
    
    
    private void executarComando(String sql) {
        String url = "jdbc:sqlite:banco_rpg.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            System.out.println("Salvo no SQLite: " + sql);
            
        } catch (Exception e) {
            System.out.println("Erro ao salvar no banco: " + e.getMessage());
        }
    }
}