package Model;

public class Colar extends Item {

	public Colar(String nome, Integer peso, Integer pontos, Integer id) {
		super(nome, peso,id);
		this.pontos = pontos;
	}
	public Colar(String nome, Integer peso, Integer pontos) {
		super(nome, peso);
		this.pontos = pontos;
	}
	
	public Colar() {
		super();
		this.pontos = 30;
	}
	
	
	@Override
    public void interagir(Personagem p) {
        p.setVida(p.getVida() + this.pontos);
        System.out.println(p.getNome() + " colocou o colar e ganhou mais vida!");
    }
	
	

}
