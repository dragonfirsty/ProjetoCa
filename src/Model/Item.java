package Model;

public abstract class Item {
	private Integer id;
	protected String nome;
	private Integer peso;
	protected Integer pontos;
	
	public Item(String nome, Integer peso) {
        this.nome = nome;
        this.peso = peso;
        this.id = 0;
    }
	
	public Item(String nome, Integer peso,Integer id) {
		super();
		this.nome = nome;
		this.peso = peso;
		this.id = id;
	}
	public Item() {
		this.id = 0;
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
