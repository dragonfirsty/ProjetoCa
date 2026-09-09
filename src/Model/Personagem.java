package Model;

import java.util.ArrayList;
import java.util.List;

public abstract class Personagem {
	protected Integer vida;
	protected String nome;
	protected Integer forca;
	private List<Item> itens;
	
	public Personagem(Integer vida, String nome, Integer forca) {
		this.vida = vida;
		this.nome = nome;
		this.forca = forca;
		this.itens = new ArrayList<Item>();
	}
	
	public void adicionarItem(Item item) {
        this.itens.add(item);
    }
	
	public void interagirComItem(Item item) {
        if (itens.contains(item)) {
            item.interagir(this);
            itens.remove(item); 
        }
    }
	
	public void interagirComItem(String nomeDoItem) {
        for (Item item : itens) {
            if (item.getNome().equalsIgnoreCase(nomeDoItem)) {
                interagirComItem(item);
                return;
            }
        }
        System.out.println("Item não encontrado.");
    }
	
	public <T extends Item> List<T> buscarItensPorTipo(Class<T> tipoClasse) {
        List<T> itensFiltrados = new ArrayList<>();
        for (Item item : this.itens) {
            if (tipoClasse.isInstance(item)) {
                itensFiltrados.add(tipoClasse.cast(item));
            }
        }
        return itensFiltrados;
    }
	
	public abstract boolean atacar(Personagem p);
	
	public Integer getVida() {
		return vida;
	}

	public void setVida(Integer vida) {
		this.vida = vida;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getForca() {
		return forca;
	}

	public void setForca(Integer forca) {
		this.forca = forca;
	}
	
	
	
}
