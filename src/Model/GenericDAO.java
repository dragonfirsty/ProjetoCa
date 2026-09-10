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

    private void executarComando(String sql) {
        // Conexão Serverless: Cria um arquivo chamado 'banco_rpg.db'
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