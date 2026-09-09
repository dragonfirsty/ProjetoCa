package Model;

public class Cacador extends Personagem{
	
	private Integer destreza;
	protected boolean mirando;
	
	public Cacador(Integer vida, String nome, Integer forca, Integer destreza) {
		super(vida, nome, forca);
		this.destreza = destreza;
	}
	public void mirar() {
		if(mirando == false) {
			mirando = true;
		}
		else
			System.out.println("Voce ja esta mirando!");
	}
	@Override
	public boolean atacar(Personagem p) {
		if(p.getVida() > 0){
			if(mirando = true) {
			p.setVida(p.getVida() - (this.forca + this.destreza * 2));
			this.mirando = false;
			return true;
		}else
			p.setVida(p.getVida() - (this.forca + this.destreza));
			return true;
		}
		else {
			return false;
		}
	}
}
	