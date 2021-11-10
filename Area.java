
import javax.swing.JPanel;
import java.awt.*;

public class Area extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int xy = 50;
    public static int whPercurso = 300;
    public static int whVagao = 10;
    public int pivoC = 295;
    public int pivoS = 195;
    public int graus;

    public Area() {
        setBounds(400, 300, 850, 450);
    }

    public void paint(Graphics g) {
        g.drawString("Percurso: ", 70, 205);
        g.drawOval(xy + 100, xy, whPercurso, whPercurso);
        g.fillOval(getX(graus), getY(graus), whVagao, whVagao);
    }

    public int getX(double anguloC) {
        double radC = anguloC * (Math.PI / 180);
        double cosseno = Math.cos(radC);
        double posicaoX = pivoC - cosseno * 165;
        return (int) Math.round(posicaoX);
    }

    public int getY(double anguloS) {
        double radS = anguloS * (Math.PI / 180);
        double seno = Math.sin(radS);
        double posicaoY = pivoS - seno * 165;
        return (int) Math.round(posicaoY);
    }
}
