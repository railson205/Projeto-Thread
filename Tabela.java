import javax.swing.JPanel;
import java.awt.*;

public class Tabela extends JPanel {
    // Object[][] data;
    
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object[][] data = {{ "Id's dos passageiros", "Situação" }, { "", ""
      }, { "", "" }, { "", "" }, { "", "" }, { "", "" }, { "", "" }, { "", "" }, { "", ""
      }, { "", "" }, { "", "" } };
     

    public Tabela() {
        setVisible(true);
        setBounds(20, 300, 420, 400);
    }

    public void paint(Graphics g) {
        g.drawRect(0, 0, 400, 330);
        g.drawLine(185, 0, 185, 330);
        for (int i = 30; i <= 330; i += 30) {
            g.drawLine(0, i, 400, i);
        }
        g.drawString(data[0][0].toString(), 35, 20);
        g.drawString(data[0][1].toString(), 255, 20);
        for(int i = 1;i<=10;i++){
            g.drawString(data[i][0].toString(), 93, (20+i*30));
            g.drawString(data[i][1].toString(), 200, (20+i*30));
        }
    }

}
