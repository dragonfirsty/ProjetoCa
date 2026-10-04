import Model.Cacador;
import Model.GenericDAO;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema RPG");

        Cacador robin = new Cacador(90, "Robin", 15, 20);
        GenericDAO dao = new GenericDAO();
        dao.criarTabela(robin);
        dao.inserirObjeto(robin);
        dao.selecionarTodosObjeto(robin);
    }
}