

import javax.swing.*;
import java.awt.Font;
import java.awt.event.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

public class App extends Thread {
    public static Semaphore qtdVaga = new Semaphore(1, true);
    public static Semaphore vagaoExecuta = new Semaphore(0);
    public static Semaphore viagemInicia = new Semaphore(0);
    public static Semaphore passageiroExecuta = new Semaphore(0);
    public static List<String> tempoEmbarque = new ArrayList<>();
    public static List<String> tempoDesembarque = new ArrayList<>();
    public static JButton buttonLog = new JButton();
    public static String mensagem = "";
    public static Area area = new Area();
    public static JFrame janela = new JFrame("Montanha-russa");
    public static Tabela tabela = new Tabela();
    public static boolean executando = false;

    // ID = 0, vagão
    public App(String ID, String te, String td) {
        super(ID);
        tempoEmbarque.add(Integer.parseInt(ID), te);
        tempoDesembarque.add(Integer.parseInt(ID), td);
    }

    public void run() {
        while (true) {

            if (getName() == "0") {
                try {
                    /*
                     * vagaoExecuta vai fazer o vagão dormir e so acorda quando todas as vagas estão
                     * ocupadas e o viagemInicia serve para indicar o começo e o fim da viagem e
                     * vagãoExecuta novamente para fazer ele dormir antes de começar um novo loop,
                     * pois ao iniciar um novo loop os novos passageiros vão já liberar para o vagão
                     * iniciar sem que os passageiros entrem no vagão
                     */
                    // primeiro é passado o Tv para um array do índice 0 que seria da thread vagão
                    int Tv = Integer.parseInt(tempoEmbarque.get(Integer.parseInt(getName())));
                    int vagas = Integer.parseInt(tempoDesembarque.get(Integer.parseInt(getName())));
                    vagaoExecuta.acquire();
                    viagemInicia.release();
                    passageiroExecuta.release(vagas);
                    percurso(Tv);
                    viagemInicia.acquire();
                    vagaoExecuta.acquire();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    /*
                     * pega a string que está na lista embarque ou desembarque com o índice do seu
                     * ID e transforma em int
                     */
                    int Te = Integer.parseInt(tempoEmbarque.get(Integer.parseInt(getName())));// embarque(tempo[ID],ID),Integer.parseInt
                    // =String->Int
                    qtdVaga.acquire();
                    embarque(Te, getName());
                    executando = false;
                    if (qtdVaga.availablePermits() == 0 && !executando)
                        vagaoExecuta.release();// vagaoExecuta=1
                    int Td = Integer.parseInt(tempoDesembarque.get(Integer.parseInt(getName())));
                    passageiroExecuta.acquire();
                    percurso_e_desembarque(Td, getName());
                    qtdVaga.release();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void percurso(int tempo) {
        attPosicaoVagao(0, 0);
        /*
         * o vagão se movimento de acordo com a qtdBola, pois ele vai definir o período
         * que o vagão vai se movimentar no percurso
         */
        int qtdBola = 360;
        for (int i = qtdBola; i > 0; i--) {
            attPosicaoVagao(1, (360 / qtdBola));
            LocalDateTime periodoAttPosicaoVagao = LocalDateTime.now()
                    .plusNanos((long) (tempo / (Math.pow(10, -9) * qtdBola)));
            while (periodoAttPosicaoVagao.isAfter(LocalDateTime.now()))
                ;
        }
        attPosicaoVagao(0, 0);
    }

    public void attPosicaoVagao(int multiplier, int add) {
        area.graus = area.graus * multiplier + add;
        janela.repaint();
    }

    public void embarque(int embarque, String ID) {
        attLogTable(ID, "tentando entrar");

        LocalDateTime tempoDoEmbarque = LocalDateTime.now().plusSeconds(embarque);
        while (tempoDoEmbarque.isAfter(LocalDateTime.now())) {
            executando = true;
            attAprecia(ID, "tentando entrar");
        };
        attLogTable(ID, "entrou");

    }

    public void percurso_e_desembarque(int desembarque, String ID) {

        attLogTable(ID, "apreciando paisagem");

        
        while (viagemInicia.availablePermits() != 0)
        attAprecia(ID, "apreciando paisagem");
        ;
        attLogTable(ID, "desembarcando");
        LocalDateTime tempoDoDesembarque = LocalDateTime.now().plusSeconds(desembarque);

        while (tempoDoDesembarque.isAfter(LocalDateTime.now()))
            ;
        attLogTable(ID, "desembarcou e esperando entrar");

    }

    public void attAprecia(String id, String e) {
        int ID = Integer.parseInt(id);
        LocalDateTime attSituacao = LocalDateTime.now().plusSeconds(1);
        tabela.data[ID][1] = e;
        janela.repaint();
        while (attSituacao.isAfter(LocalDateTime.now()))
            ;
        attSituacao = LocalDateTime.now().plusSeconds(1);

        tabela.data[ID][1] = "";
        janela.repaint();
        while (attSituacao.isAfter(LocalDateTime.now()))
            ;
    }

    public static void attLogTable(String id, String situations) {
        mensagem = "> " + id + " " + situations + "\n\n" + mensagem;
        int ID = Integer.parseInt(id);
        tabela.data[ID][1] = situations;
        janela.repaint();
        buttonLog.doClick();
    }

    // construção dos objetos e da tela do programa
    public static void main(String[] args) {
        int Y = 87, Height = 20;

        try {
            // parte em que vai ser fornecido a quantidade de vagas, o tempo de viagem e
            // onde vai iniciar a thread vagão

            JFrame popUpFrame = new JFrame();
            String vagas = (String) JOptionPane.showInputDialog(popUpFrame, "Coloque a quantidade de vagas:", 2);
            String Tv = (String) JOptionPane.showInputDialog(popUpFrame, "Coloque o tempo de viagem:", 30);
            qtdVaga.drainPermits();
            qtdVaga.release(Integer.parseInt(vagas));

            App vagao = new App("0", Tv, vagas);
            vagao.start();

        } finally {

            // parte de criação da tela do programa
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setSize(1300, 800);
            janela.setLayout(null);
            janela.setVisible(true);
            janela.setLocationRelativeTo(null);

            // definição de textos e seus locais de texto
            Label labelTempo_Embarque = new Label("Tempo de Embarque:", 20, Y, 150, Height, 12);
            Label labelTempo_Desembarque = new Label("Tempo de Desembarque:", 250, Y, 150, Height, 12);
            Label labelAddPassageiro = new Label("Adicionar Passageiros:", 20, 40, 300, 30, 20);
            TextField textField_Tempo_Embarque = new TextField(150, Y, 80, Height);
            TextField textField_Tempo_Desembarque = new TextField(400, Y, 80, Height);
            JButton buttonAdd_Passageiro = new JButton("Adicionar passageiro");
            JTextArea messageLog = new JTextArea();
            JPanel log = new JPanel();
            JSeparator separatorHorizontal = new JSeparator();

            separatorHorizontal.setBounds(0, 250, 1920, 5);

            // configurações do botão de adicionar threads
            buttonAdd_Passageiro.setBounds(550, 80, 170, Height + 10);

            // Adição de novas threads
            buttonAdd_Passageiro.addActionListener(new ActionListener() {
                String Tempo_Embarque, Tempo_Desembarque;
                int i = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    Tempo_Embarque = textField_Tempo_Embarque.getText();
                    Tempo_Desembarque = textField_Tempo_Desembarque.getText();
                    i++;
                    tabela.data[i][0] = i;
                    tabela.data[i][1] ="esperando entrar";
                    janela.repaint();
                    App passageiro = new App(Integer.toString(i), Tempo_Embarque, Tempo_Desembarque);
                    passageiro.start();
                }
            });

            // definindo o tamanho do log e modificando a fonte do texto que vai no log
            log.setBounds(950, 30, 300, 180);
            log.setLayout(new BoxLayout(log, BoxLayout.Y_AXIS));
            log.setAutoscrolls(true);
            messageLog.setEditable(false);
            messageLog.setFont(new Font(messageLog.getFont().getFamily(), Font.PLAIN, 15));

            // botão do log para mudar o log a cada att da situação
            buttonLog.setVisible(false);
            buttonLog.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    messageLog.setText(mensagem);
                }
            });

            // adição dos objetos
            log.add(messageLog);
            janela.add(labelTempo_Embarque);
            janela.add(labelTempo_Desembarque);
            janela.add(textField_Tempo_Embarque);
            janela.add(textField_Tempo_Desembarque);
            janela.add(buttonAdd_Passageiro);
            janela.add(log);
            janela.add(buttonLog);
            janela.add(separatorHorizontal);
            janela.add(area);
            janela.add(tabela);
            janela.add(labelAddPassageiro);
            janela.repaint();
        }
    }
}