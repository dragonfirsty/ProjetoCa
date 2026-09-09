package Model;

public abstract class Item {
	protected String nome;
	private Integer peso;
	protected Integer pontos;
	
	public Item(String nome, Integer peso) {
		super();
		this.nome = nome;
		this.peso = peso;
	}
	public Item() {
        this.nome = "Item Não identificado";
        this.peso = 1;
    }
	
	public abstract void interagir(Personagem p);

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getPeso() {
		return peso;
	}

	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	
	public Integer getPontos() {
		return this.pontos;
	}
	
}
