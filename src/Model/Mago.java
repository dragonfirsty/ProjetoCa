package Model;

public class Mago extends Personagem {
	
	private Integer mana;
	
	public Mago(Integer vida, String nome, Integer forca, Integer mana) {
		super(vida, nome, forca);
		this.mana = mana;
		// TODO Auto-generated constructor stub
	}
	
	public boolean recarregarMana() {
		if(this.mana < 100) {
			this.mana += 10;
			return true;
		}
		return false;
	}
	

	@Override
	public boolean atacar(Personagem p) {
		if(p.getVida() > 0) {
			p.setVida(p.getVida() - (this.getForca() + this.mana));
			return true;
		}else {
			return false;
		}
	}
	public Integer getMana() {
		return mana;
	}

	public void setMana(Integer mana) {
		this.mana = mana;
	}

	

}
