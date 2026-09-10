package Model;

public class PocaoMana extends Pocao{
	
	public PocaoMana(String nome, Integer peso, Integer pontos, Integer id) {
		super(nome,peso, pontos, id);
		
	}

	public PocaoMana(String nome, Integer peso, Integer pontos) {
		super(nome,peso, pontos);
		
	}
	public PocaoMana() {
		super();
		this.nome = "Poção de mana";
		this.pontos = 15;
	}
	
	@Override
    public void interagir(Personagem p) {
		if(p instanceof Mago) {
		Mago mago = (Mago) p;
		mago.setMana(mago.getMana() + this.pontos);
        System.out.println(p.getNome() + " Recuperou Mana com a poção");
		}
		else
			System.out.println(p.getNome() + " bebeu a poção, mas não sentiu efeito nenhum");
    }

}
