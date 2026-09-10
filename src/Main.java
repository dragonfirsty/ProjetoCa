import Model.Cacador;
import Model.GenericDAO;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema RPG...");
        
        // 1. Cria o arquivo do banco e a tabela do caçador
        prepararBancoDados();
        
        // 2. Instancia o seu personagem
        // Como o seu primeiro construtor já faz this.id = 0 internamente,
        // não precisamos passar o 0 aqui.
        Cacador robin = new Cacador(90, "Robin", 15, 20); 
        
        // 3. Testa o INSERT (Create)
        GenericDAO dao = new GenericDAO();
        dao.inserirObjeto(robin);
        
        // 4. Testa o SELECT (Read)
        dao.selecionarTodosObjeto(robin); 
    }
    
    // Método auxiliar para não dar erro de tabela inexistente no primeiro teste
    private static void prepararBancoDados() {
        String url = "jdbc:sqlite:banco_rpg.db";
        String sqlCacador = "CREATE TABLE IF NOT EXISTS cacador ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "vida INTEGER,"
                + "nome TEXT,"
                + "forca INTEGER,"
                + "destreza INTEGER"
                + ");";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCacador);
        } catch (Exception e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }
}