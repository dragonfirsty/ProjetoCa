package Model;

public class PocaoVida extends Pocao{
	public PocaoVida(String nome, Integer peso, Integer pontos) {
		super(nome,peso, pontos);
		
	}
	
	public PocaoVida() {
		super();
		this.nome = "Poção de vida";
		this.pontos = 15;
	}
	
	@Override
    public void interagir(Personagem p) {
        p.setVida(p.getVida() + this.pontos);
        System.out.println(p.getNome() + " Recuperou vida com a poção");
    }
	
}
