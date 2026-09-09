import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import Model.Guerreiro;
import Model.Mago;
import Model.Personagem;
import Model.PocaoMana;
import Model.PocaoVida;
import Model.Arma;
import Model.Cacador;
import Model.Colar;

public class Main {

	
	
	public static void main(String[] args) {
		/*
		// Referencia forte! Uma instancia de referencia forte
		
		//Guerreiro g = new Guerreiro(100,"canaroso",100);
		// Polimorfismo é quando uma variavel de um supertipo(tipo pai)
		//referencia quaisquer um dos subtipos em tempo de execução
//		Personagem m = new Mago(100,"fulano",100,100);
//		
//		// referência fraca
		// polimorfismo de subtipos
		
		//em tempo de execução
//		Personagem p = new Guerreiro(100,"cicrano",100);
//
//		p.atacar(m);
//		
//		p = m;
//		p.atacar(m);
		
		//p = new Arqueiro(100, "Legolas",100);
//		
//		p.atacar(m);
//		
		
//		List<Personagem> personagens = new ArrayList<Personagem>();
//		
		//Set<Personagem> xs = new HashSet<Personagem>();
		
//		personagens.add(p);
//		personagens.add(p);
//		personagens.add(p);
//		
//		System.out.println(p.equals(m));
//		
//		System.out.println(personagens.size());
		
		
		//p.r
		//Personagem p3 = new Personagem(100,"luis",100);
	
		
		
		Scanner s = new Scanner(System.in);
		int menu = 0;
		int personagem = 0;
		System.out.println("Jogo Fezes De luta");
		Personagem p1, p2;
		while(menu != 100) {
			System.out.println("Select your hero!!! tanana");
			personagem = s.nextInt();
			
			switch (personagem) {
			case 0:
				p1 = new Personagem(100, "Goku", 1000);
				System.out.println(p1);
				break;

			default:
				break;
			}
		}
		
		
		Personagem goku  = new Personagem(100,"Goku",1000);
		Personagem ku = new Personagem(1,"Kuririm",1);
		System.out.println(goku);
	
		ku.atacar(goku);
		System.out.println(goku.atacar(ku));
		System.out.println(ku.getVida());
		System.out.println(goku.atacar(ku));
		
		System.out.println(goku.getVida() +  "!!");
		System.out.println(ku.getVida());
		*/
		
		
		// Instanciando os personagens (Polimorfismo de Subtipos)
        Guerreiro guerreiro = new Guerreiro(100, "Arthur", 30);
        Mago mago = new Mago(80, "Merlin", 10, 50); // vida, nome, forca, mana
        Cacador cacador = new Cacador(90, "Robin", 15, 20); // vida, nome, forca, destreza

        // Instanciando os itens e poções
        Arma espada = new Arma("Espada Montante", 5, 20);
        Colar amuleto = new Colar("Amuleto do Vigor", 2, 50);
        PocaoVida pocaoCura = new PocaoVida("Poção de Cura Maior", 1, 40);
        PocaoMana pocaoMagica1 = new PocaoMana("Frasco de Mana", 1, 30);
        PocaoMana pocaoMagica2 = new PocaoMana("Frasco de Mana", 1, 30);

        System.out.println("\n--- TESTE 1: Guerreiro interagindo com Arma ---");
        // Valida: Interação básica e aumento de atributo base
        guerreiro.adicionarItem(espada);
        System.out.println("Força antes: " + guerreiro.getForca());
        guerreiro.interagirComItem(espada);
        System.out.println("Força depois: " + guerreiro.getForca());

        System.out.println("\n--- TESTE 2: Mago usando Poção de Mana (Sucesso) ---");
        // Valida: O instanceof liberando o efeito exclusivo para a classe Mago
        mago.adicionarItem(pocaoMagica1);
        System.out.println("Mana antes: " + mago.getMana());
        mago.interagirComItem(pocaoMagica1);
        System.out.println("Mana depois: " + mago.getMana());

        System.out.println("\n--- TESTE 3: Caçador tentando usar Poção de Mana (Falha) ---");
        // Valida: O encapsulamento e a trava do instanceof bloqueando itens incompatíveis
        cacador.adicionarItem(pocaoMagica2);
        cacador.interagirComItem(pocaoMagica2); 

        System.out.println("\n--- TESTE 4: Caçador interagindo com Colar ---");
        // Valida: Herança de item genérico afetando atributos da classe mãe (Personagem)
        cacador.adicionarItem(amuleto);
        System.out.println("Vida do Caçador antes: " + cacador.getVida());
        cacador.interagirComItem(amuleto);
        System.out.println("Vida do Caçador depois: " + cacador.getVida());

        System.out.println("\n--- TESTE 5: Guerreiro usando Poção de Vida ---");
        // Valida: Acesso a atributos herdados e sobrecarga na busca do item
        guerreiro.adicionarItem(pocaoCura);
        guerreiro.interagirComItem("Poção de Cura Maior");

        System.out.println("\n--- TESTE 6: Combate - Mago atacando Guerreiro ---");
        // Valida: Sobrescrita de método (@Override) calculando dano com Mana
        System.out.println("Vida do Guerreiro antes do ataque mágico: " + guerreiro.getVida());
        mago.atacar(guerreiro); 
        System.out.println("Vida do Guerreiro após receber ataque mágico: " + guerreiro.getVida());

        System.out.println("\n--- TESTE 7: Combate - Caçador Mirando e Atacando ---");
        // Valida: Método exclusivo da classe filha buffando a Sobrescrita do ataque
        System.out.println("Vida do Mago antes da flechada: " + mago.getVida());
        cacador.mirar(); // Ativa o multiplicador de destreza
        cacador.atacar(mago);
        System.out.println("Vida do Mago após receber tiro crítico: " + mago.getVida());
     
	}
}
