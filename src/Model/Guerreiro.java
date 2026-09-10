package Model;

public class Guerreiro extends Personagem{

	
	public Guerreiro(Integer vida, String nome, Integer forca,Integer id) {
		super(vida, nome, forca,id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean atacar(Personagem p) {
		if(p.getVida() > 0) {
			p.setVida(p.getVida() - this.forca);
			return true;
		}else {
			return false;
		}
	}


}
