import Model.Arma;
import Model.Cacador;
import Model.Guerreiro;
import Model.GenericDAO;
import Model.Mago;
import Model.PocaoMana;
import Model.PocaoVida;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema RPG");

        GenericDAO dao = new GenericDAO();

        Guerreiro guerreiro = new Guerreiro(100, "Aragorn", 20);
        Mago mago = new Mago(80, "Gandalf", 10, 30);
        Cacador cacador = new Cacador(90, "Robin", 15, 20);

        guerreiro.adicionarItem(new Arma("Espada", 5, 12));
        mago.adicionarItem(new PocaoMana("Pocao de mana", 1, 20));
        cacador.adicionarItem(new Arma("Arco", 3, 8));
        cacador.adicionarItem(new PocaoVida("Pocao de vida", 1, 25));

        guerreiro.interagirComItem("Espada");
        mago.interagirComItem("Pocao de mana");
        cacador.interagirComItem("Arco");
        cacador.interagirComItem("Pocao de vida");

        System.out.println("\n=== PERSONAGENS EM JOGO ===");
        System.out.println("Guerreiro: " + guerreiro.getNome() + " | Vida: " + guerreiro.getVida()
            + " | Forca: " + guerreiro.getForca());
        System.out.println("Mago: " + mago.getNome() + " | Vida: " + mago.getVida()
            + " | Forca: " + mago.getForca() + " | Mana: " + mago.getMana());
        System.out.println("Cacador: " + cacador.getNome() + " | Vida: " + cacador.getVida()
            + " | Forca: " + cacador.getForca());

        dao.criarTabela(guerreiro);
        dao.criarTabela(mago);
        dao.criarTabela(cacador);

        dao.inserirObjeto(guerreiro);
        dao.inserirObjeto(mago);
        dao.inserirObjeto(cacador);

        System.out.println("\n=== REGISTROS SALVOS NO BANCO ===");
        System.out.println("Guerreiros:");
        dao.selecionarTodosObjeto(guerreiro);
        System.out.println("Magos:");
        dao.selecionarTodosObjeto(mago);
        System.out.println("Cacadores:");
        dao.selecionarTodosObjeto(cacador);
    }
}