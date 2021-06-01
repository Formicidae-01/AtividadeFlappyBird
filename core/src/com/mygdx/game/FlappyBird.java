package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.Rectangle;
import java.util.Random;

import sun.security.ssl.Debug;

public class FlappyBird extends ApplicationAdapter {
	//Renderização de sprite
	SpriteBatch batch;
	//Sprite utilizado como fundo da tela
	Texture fundo;
	//Array de sprites para o pássaro, animação
	Texture[] passaros;
	//Sprite de Game Over
	Texture gameOver;
	//Texturas para os canos do topo e de baixo
	Texture canoTopo, canoBaixo;
	//Texto que permanece na tela exibindo a pontuação
	BitmapFont textoPontuacao, textoInicio, textoMelhorPonto;

	//Largura e altura da tela do dispositivo
	float largura,altura;

	//Valores int que guardam a gravidade do personagem e sua pontuação e qual sprite será exibido
	int gravidade = 0, pontos = 0, melhorPontuacao = 0;

	//Float que determina qual frame da animação será utilizado
	float variacao = 0;

	//Valor float que define qual será a posição vertical inicial do pássaro
	float posicaoInicialVertical = 0;

	//Valores que guardam a posição horizontal e vertical dos canos, além do espaço entre eles
	float canoHorizontal, canoVertical, canoEspaço;

	//Estado do jogo
	//0=Início, 1=Jogo, 2=GameOver
	int estadoJogo = 0;

	//Boolean que identifica se o personagem passou por um cano ou não
	boolean passouCano = false;
	//Variável que gera um valor aleatório
	Random random;

	//Renderizador de um formato
	ShapeRenderer shapeRenderer;
	//Variável círculo, serve como colisor do pássaro
	Circle circuloPassaro;
	//Retângulo dos canos, servem como colisores
	com.badlogic.gdx.math.Rectangle retanguloCanoCima, retanguloCanoBaixo;
	//private int ;
	//private int ;

	//Variáveis de som, para serem utilizados no jogo
	Sound somAsa, somBatida, somPonto;

	Preferences preferences;

	@Override
	public void create () {
		//Métodos executados na iniciação do aplicativo
		//Atribui as texturas
		InicializaTexturas();
		//Atribui outros objetos como textos e colisores
		InicializaObjetos();
	}

	@Override
	public void render () {
        //Métodos que ocorrem constantemente
		//Verifica o estado do jogo
	    EstadoDoJogo();
	    //Desenha as texturas/sprites na tela
	    DesenharTexturas();
	    //Verifica a pontuação do jogo
	    ContagemPontos();
	    //Verifica a colisão do jogo, apenas se o estado do jogo for 1 (gameplay)
		if (estadoJogo == 1)
		{
			Colisao();
		}
	}
	
	@Override
	public void dispose () {

	}

	void InicializaObjetos()
	{
		//Criando nova variável do tipo random
		random = new Random();
		//Criando renderizador de sprites
		batch = new SpriteBatch();

		//Determinando "largura" como a largura da tela do disposivito
		largura = Gdx.graphics.getWidth();
		//Determinando "altura" como altura da tela do dispositivo
		altura = Gdx.graphics.getHeight();
		//Determinando a posição vertical inicial na qual o pássaro irá surgir (no meio da tela)
		posicaoInicialVertical = altura/2;
		//Determinando a posição horizontal inicial na qual os canos irão surgir
		canoHorizontal = largura;
		//Determinando o espaço inicial entre os canos
		canoEspaço = 400;

		//Atribuindo o texto de pontuação, definindo seu tamanho e cor
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(8);

		//Atribuindo o texto de melhor pontuação, definindo seu tamanho e cor
		textoMelhorPonto = new BitmapFont();
		textoMelhorPonto.setColor(Color.WHITE);
		textoMelhorPonto.getData().setScale(4);

		//Atribuindo o texto de início, definindo seu tamanho e cor
		textoInicio = new BitmapFont();
		textoInicio.setColor(Color.YELLOW);
		textoInicio.getData().setScale(5);

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new com.badlogic.gdx.math.Rectangle();
		retanguloCanoBaixo = new com.badlogic.gdx.math.Rectangle();
		//retanguloCanoCima = new Rectangle();
		//retanguloCanoBaixo = new Rectangle();

		//Atribuindo os áudios às variáveis de som
		somAsa = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somBatida = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPonto = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		preferences = Gdx.app.getPreferences("flappyBird");

		melhorPontuacao = preferences.getInteger("melhorPontuacao", 0);

	}

	void InicializaTexturas()
	{
		//Atribuindo a textura fundo à variável fundo
		fundo = new Texture ("fundo.png");
		//Criando uma array de sprites, para animar o pássaro
		passaros = new Texture[3];
		//Atribuindo os sprites a cada variável da array
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

        //Atribuindo a textura dos canos
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

		//Atribuindo a textura do gameOver
		gameOver = new Texture("game_over.png");
	}

	void DesenharTexturas()
	{
		//Inicializando a renderização
		batch.begin();

		//Desenhando o fundo
		batch.draw(fundo,0,0, largura,altura);
		//Desenhando o pássaro na tela
		batch.draw(passaros[(int)variacao], 50, posicaoInicialVertical, 100,70);
		//Desenhando os canos
        batch.draw(canoBaixo, canoHorizontal, altura / 2 - canoBaixo.getHeight() - canoEspaço / 2);
        batch.draw(canoTopo, canoHorizontal, altura / 2 + canoEspaço / 2 + canoVertical);

        //Desenha recursos de acordo com os estados do jogo
        switch (estadoJogo)
		{
			case 0:
			{
				//Desenhando o texto de toque caso o estado seja 0
				textoInicio.draw(batch, "Toque para iniciar!", largura/2, altura/2 + 200, 1,1,true);
			}
			break;

			case 1:
			{
				//Desenhando o texto de pontuação caso o estado seja 1
				textoPontuacao.draw(batch, String.valueOf(pontos), largura / 2, altura - 100, 1,1,true);
			}
            break;

			case 2:
			{
				//Desenhando o game over caso o estado seja 2
				batch.draw(gameOver, largura / 2 - 225, altura/2 + 200, 450,100);
				//Exibindo a melhor pontuação do jogador
				textoMelhorPonto.draw(batch, "Melhor pontuação: " + String.valueOf(melhorPontuacao), largura/2, altura/2 + 150, 1,1,true);
				//Exibindo o texto indicando para reiniciar o jogo
                textoInicio.draw(batch, "Toque para reiniciar!", largura/2, altura/2 - 400, 1,1,true);
			}
		}
        //Finalizando a renderização
		batch.end();
	}

	void EstadoDoJogo()
    {
        //Boolean que indica se houve toque na tela nesse quadro
		boolean toqueTela = Gdx.input.justTouched();

		//Aumentando o valor "Variação", usado para animar o pássaro
		variacao += Gdx.graphics.getDeltaTime() * 10;

		//Reinicia o valor "Variação" caso seja maior ou igual a 2, para reiniciar a animação
		if (variacao >=2)
		{
			variacao = 0;
		}

		//Executa funções de acordo com o estado do jogo
    	switch (estadoJogo)
		{
			case 0:
			{
				//Começa o jogo caso o estado seja 0
				if (toqueTela)
				{
					//Reproduz o som de voo do pássaro
					somAsa.play();
					//Modifica o valor de gravidade do pássaro, jogando-o pra cima
					gravidade = -15;
					//Altera o estado do jogo para 1
					estadoJogo = 1;
				}
			}
			break;

			case 1:
			{
				//Modifica a variável "canoHorizontal", atualizando a posição horizontal dos canos
				canoHorizontal -= Gdx.graphics.getDeltaTime() * 350;
				//Reiniciando a posição do cano caso esteja no canto da tela
				if (canoHorizontal < -canoBaixo.getHeight())
				{
					//Colocando a posição do cano no canto direito da tela
					canoHorizontal = largura;
					//Randomizando a posição vertical do cano
					canoVertical = random.nextInt(400) -200;
					//Reinicia a variável que indica se o jogador passou o cano
					passouCano = false;
				}

				//Executa um salto do pássaro ao pressionar a tela
				if (toqueTela)
				{
					//Reproduz o som de voo do pássaro
					somAsa.play();
					//Modifica o valor de gravidade, fazendo com que o pássaro suba
					gravidade = -15;
				}

				//Executa função caso a possição vertical do pássaro seja maior que 0 (significa que ele ainda está vizível e não caiu o suficiente para sumir)
				if (posicaoInicialVertical > 0)
				{
					//Diminui o valor de posição vertical, fazendo com que o pássaro caia
					posicaoInicialVertical = posicaoInicialVertical - gravidade;
				}

				//Aumenta o valor de gravidade, deixando-a mais forte
				gravidade++;
			}
			break;

			//Executa função caso o estado do jogo seja 2
			case 2:
			{
				//Executa função ao tocar na tela
				if(toqueTela)
				{
					//Reiniciando o estado do jogo
					estadoJogo = 0;
					//Reiniciando a pontuação do jogador
					pontos = 0;
					//Reiniciando a gravidade, para que o jogador não caia muito rápido ao recomeçar
                    gravidade = 0;
                    //Reiniciando a posição vertical do jogador
					posicaoInicialVertical = altura/2;
					//Reiniciando a posição dos canos
					canoHorizontal = largura;
				}

				//Executa função caso a possição vertical do pássaro seja maior que 0 (significa que ele ainda está vizível e não caiu o suficiente para sumir)
				if (posicaoInicialVertical > 0)
				{
					//Diminui o valor de posição vertical, fazendo com que o pássaro caia
					posicaoInicialVertical = posicaoInicialVertical - gravidade;
				}

				//Aumenta o valor de gravidade, deixando-a mais forte
				gravidade++;
			}
			break;
		}
    }

    void ContagemPontos()
    {
    	//Executa função caso o pássaro esteja ultrapassando os canos
        if (canoHorizontal < 50 - passaros[0].getWidth())
        {
        	//Executa função caso o jogador ainda não tenha passado o cano
            if (!passouCano)
            {
            	//Reproduz o som de pontuação
            	somPonto.play();
            	//Aumenta a pontuação
                pontos++;
                //Indica que o jogador passou o cano, dessa forma, essa função não se repetirá mais
                passouCano = true;
            }
        }
    }

    void Colisao()
    {
    	//Atribuindo o colisor do pássaro, seu tamanho e posição
        circuloPassaro.set((50 + passaros[0].getWidth() / 2), posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        //Atribuindo o colisor dos canos
        retanguloCanoBaixo.set(canoHorizontal, altura / 2 + canoBaixo.getHeight() + canoEspaço / 2 + canoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());
        retanguloCanoCima.set(canoHorizontal, altura / 2 - canoBaixo.getHeight() - canoEspaço / 2 + canoVertical, canoTopo.getWidth(), canoTopo.getHeight());

        //Booleans que identificam se houve colisão com os canos de cima ou baixo
        boolean bateuCanoTopo = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
        boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

        //Executa função caso tenha ocorrido colisão com os canos ou o pássaro tenha caído no chão
        if (bateuCanoBaixo || bateuCanoTopo || posicaoInicialVertical < 0 && estadoJogo == 1)
		{
			//Verifica se a pontuação atual é maior que a melhor pontuação já feita pelo jogador
			if (pontos > melhorPontuacao)
			{
				//Atribuindo a nova melhor pontuação do jogador
				melhorPontuacao = pontos;
				preferences.putInteger("melhorPontuacao", melhorPontuacao);
			}

			//Reproduzindo som de colisão
			somBatida.play();
			//Alterando o estado do jogo para 2
			estadoJogo = 2;
		}
    }
}
