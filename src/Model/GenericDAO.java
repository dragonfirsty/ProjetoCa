package Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.lang.reflect.Field;


public class GenericDAO {
	private void executarComando(String sql) {
	    // Exemplo usando MySQL. Ajuste a URL, usuário e senha conforme seu banco.
	    String url = "jdbc:mysql://localhost:3306/banco_rpg";
	    String usuario = "root";
	    String senha = "";

	    try (Connection conn = DriverManager.getConnection(url, usuario, senha);
	         Statement stmt = conn.createStatement()) {
	        
	        // Executa o insert, update ou delete real no banco
	        stmt.executeUpdate(sql);
	        System.out.println("Comando executado com sucesso: " + sql);
	        
	    } catch (SQLException e) {
	        System.out.println("Erro ao executar SQL no banco: " + e.getMessage());
	    }
	}
	
	public void inserirObjeto(Object o) {
	    Class<?> clazz = o.getClass();
	    String nomeClasse = clazz.getSimpleName().toLowerCase(); 

	    StringBuilder sql = new StringBuilder("insert into ");
	    sql.append(nomeClasse).append(" (");
	    
	    Field[] campos = clazz.getDeclaredFields();
	    
	    // Monta os nomes das colunas
	    for (int i = 0; i < campos.length; i++) {
	        sql.append(campos[i].getName());
	        if (i < campos.length - 1) sql.append(", ");
	    }
	    
	    sql.append(") values (");
	    
	    // Monta os valores
	    for (int i = 0; i < campos.length; i++) {
	        campos[i].setAccessible(true);
	        try {
	            Object valor = campos[i].get(o);
	            // Se for texto, coloca entre aspas simples
	            if (valor instanceof String) {
	                sql.append("'").append(valor).append("'");
	            } else {
	                sql.append(valor);
	            }
	            if (i < campos.length - 1) sql.append(", ");
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }
	    sql.append(");");
	    
	    // Agora enviamos para o banco em vez de apenas imprimir
	    executarComando(sql.toString());
	}
}
