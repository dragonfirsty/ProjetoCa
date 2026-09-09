package Model;

public class Arma extends Item  {

	public Arma(String nome, Integer peso, Integer pontos) {
		super(nome, peso);
		this.pontos = pontos;
	}
	public Arma() {
		super();
		this.pontos = 10;
	}
	
	
	@Override
    public void interagir(Personagem p) {
        p.setForca(p.getForca() + this.pontos);
        System.out.println(p.getNome() + " equipou a arma e aumentou a força!");
    }
	
}

