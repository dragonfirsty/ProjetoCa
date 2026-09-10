package Model;

public abstract class Pocao  extends Item{
	
	public Pocao(String nome, Integer peso, Integer pontos, Integer id) {
		super(nome,peso,id);
		this.pontos = pontos;
	}
	public Pocao(String nome, Integer peso, Integer pontos) {
		super(nome,peso);
		this.pontos = pontos;
	}
	
	
	public Pocao() {
		super();
		this.nome = "Poção misteriosa";
		this.pontos = 15;
	}

}
