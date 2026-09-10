package Model;

public class Guerreiro extends Personagem{

	
	public Guerreiro(Integer vida, String nome, Integer forca) {
        super(vida, nome, forca);
    }
	public Guerreiro(Integer id, Integer vida, String nome, Integer forca) {
        super(id, vida, nome, forca);
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
