package pokesal.batalha;

import pokesal.modelo.AcaoTurno;
import pokesal.modelo.Pokesal;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoGolpe;
import pokesal.modelo.TipoItem;
import pokesal.modelo.Treinador;

/**
 * Controla uma batalha 1x1 por turnos no estacionamento da UCSal.
 */
public class Batalha {

  private final Treinador treinador1;
  private final Treinador treinador2;
  private final Terreno terreno;
  private final CalculadoraDano calculadoraDano;
  private int numeroTurno;
  private final StringBuilder relatorio;
  private int hpAntesDoStatus1;
  private int hpAntesDoStatus2;

  /**
   * Prepara a batalha entre dois treinadores no terreno informado.
   *
   * @param treinador1 primeiro treinador (último desempate se SPD e ATK iguais)
   * @param treinador2 segundo treinador
   * @param terreno efeito de terreno do estacionamento
   */
  public Batalha(Treinador treinador1, Treinador treinador2, Terreno terreno) {
    if (treinador1 == null || treinador2 == null) {
      throw new IllegalArgumentException("Os dois treinadores sao obrigatorios.");
    }
    if (terreno == null) {
      throw new IllegalArgumentException("O terreno e obrigatorio.");
    }
    this.treinador1 = treinador1;
    this.treinador2 = treinador2;
    this.terreno = terreno;
    this.calculadoraDano = new CalculadoraDano();
    this.numeroTurno = 1;
    this.relatorio = new StringBuilder();
    this.hpAntesDoStatus1 = 0;
    this.hpAntesDoStatus2 = 0;
  }

  /**
   * Define a ordem de ataque do turno pela SPD efetiva.
   *
   * <p>O enunciado não diz o que fazer se a SPD empatar. Correção de
   * requisito: nesse caso começa quem tiver maior ATK efetivo. Se o ATK
   * também empatar, o treinador 1 age primeiro.
   *
   * @return array com o primeiro e o segundo a agir
   */
  public Treinador[] obterOrdemDeAtaque() {
    Pokesal pokesal1 = this.treinador1.getPokesal();
    Pokesal pokesal2 = this.treinador2.getPokesal();
    int spd1 = pokesal1.getSpdEfetivo();
    int spd2 = pokesal2.getSpdEfetivo();
    if (spd1 > spd2) {
      return new Treinador[] {this.treinador1, this.treinador2};
    }
    if (spd2 > spd1) {
      return new Treinador[] {this.treinador2, this.treinador1};
    }
    int atk1 = pokesal1.getAtkEfetivo();
    int atk2 = pokesal2.getAtkEfetivo();
    if (atk2 > atk1) {
      return new Treinador[] {this.treinador2, this.treinador1};
    }
    return new Treinador[] {this.treinador1, this.treinador2};
  }

  /**
   * @return treinador que age primeiro neste instante
   */
  public Treinador obterPrimeiroAAgir() {
    return obterOrdemDeAtaque()[0];
  }

  /**
   * Executa o golpe elemental (com tipo, terreno e combo).
   *
   * @param atacante treinador que ataca
   * @param defensor treinador que recebe
   * @return dano causado
   */
  public int executarAtaque(Treinador atacante, Treinador defensor) {
    return executarAtaque(atacante, defensor, TipoGolpe.ELEMENTAL);
  }

  /**
   * Executa um golpe, aplica dano e, se for elemental, tenta infligir status.
   *
   * @param atacante treinador que ataca
   * @param defensor treinador que recebe
   * @param golpe elemental ou Investida
   * @return dano causado
   */
  public int executarAtaque(Treinador atacante, Treinador defensor,
      TipoGolpe golpe) {
    Pokesal origem = atacante.getPokesal();
    Pokesal alvo = defensor.getPokesal();
    boolean combo = origem.isCombo(golpe);
    int dano = this.calculadoraDano.calcular(origem, alvo, this.terreno, golpe,
        combo);
    alvo.receberDano(dano);
    if (combo) {
      registrar("COMBO! " + origem.getNome() + " causou " + dano
          + " de dano em " + alvo.getNome() + ".");
    } else {
      registrar(origem.getNome() + " causou " + dano + " de dano em "
          + alvo.getNome() + ".");
    }
    origem.registrarGolpe(golpe);
    if (!alvo.estaNocauteado()) {
      if (golpe == TipoGolpe.ELEMENTAL) {
        aplicarStatusDoGolpe(origem, alvo);
      }
    } else {
      registrar(alvo.getNome() + " foi nocauteado!");
    }
    return dano;
  }

  /**
   * Executa a ação escolhida pelo treinador. Usar item consome o turno e zera
   * o combo.
   *
   * @param ator quem está agindo
   * @param alvo oponente
   * @param acao ação do turno
   * @param item item usado, ou null se não for o caso
   */
  public void executarAcao(Treinador ator, Treinador alvo, AcaoTurno acao,
      TipoItem item) {
    if (ator.getPokesal().estaNocauteado()) {
      return;
    }
    if (acao == AcaoTurno.ATACAR) {
      executarAtaque(ator, alvo, TipoGolpe.ELEMENTAL);
    } else if (acao == AcaoTurno.INVESTIDA) {
      executarAtaque(ator, alvo, TipoGolpe.INVESTIDA);
    } else if (acao == AcaoTurno.USAR_ITEM) {
      ator.usarItem(item);
      ator.getPokesal().resetarCombo();
      registrar(ator.getNome() + " usou " + item + ".");
    }
  }

  /**
   * Resolve o turno: ordem por SPD, ações e efeitos de fim de turno.
   *
   * @param acao1 ação do treinador 1
   * @param item1 item do treinador 1 (pode ser null)
   * @param acao2 ação do treinador 2
   * @param item2 item do treinador 2 (pode ser null)
   */
  public void resolverTurno(AcaoTurno acao1, TipoItem item1, AcaoTurno acao2,
      TipoItem item2) {
    Treinador[] ordem = obterOrdemDeAtaque();
    for (int i = 0; i < ordem.length; i++) {
      if (batalhaEncerrada()) {
        return;
      }
      Treinador ator = ordem[i];
      if (ator == this.treinador1) {
        executarAcao(ator, this.treinador2, acao1, item1);
      } else {
        executarAcao(ator, this.treinador1, acao2, item2);
      }
    }
    if (!batalhaEncerrada()) {
      finalizarTurno();
    }
  }

  /**
   * Regeneração do canteiro e dano de status no fim do turno.
   *
   * <p>Não existe limite de turnos: a luta segue até alguém cair.
   */
  public void finalizarTurno() {
    aplicarRegenCanteiro(this.treinador1);
    aplicarRegenCanteiro(this.treinador2);
    this.hpAntesDoStatus1 = this.treinador1.getPokesal().getHpAtual();
    this.hpAntesDoStatus2 = this.treinador2.getPokesal().getHpAtual();
    this.treinador1.getPokesal().aplicarEfeitosStatusFimTurno();
    this.treinador2.getPokesal().aplicarEfeitosStatusFimTurno();
    this.numeroTurno++;
  }

  /**
   * @return true se algum Pokésal já está nocauteado
   */
  public boolean batalhaEncerrada() {
    return this.treinador1.getPokesal().estaNocauteado()
        || this.treinador2.getPokesal().estaNocauteado();
  }

  /**
   * Devolve o vencedor. A batalha não empata: se os dois caírem no mesmo
   * tick de status, ganha quem tinha mais HP imediatamente antes. Se o HP
   * era igual, ganha o treinador 1.
   *
   * @return treinador vencedor, ou null se a luta ainda não acabou
   */
  public Treinador obterVencedor() {
    boolean nocaute1 = this.treinador1.getPokesal().estaNocauteado();
    boolean nocaute2 = this.treinador2.getPokesal().estaNocauteado();
    if (!nocaute1 && !nocaute2) {
      return null;
    }
    if (nocaute1 && !nocaute2) {
      return this.treinador2;
    }
    if (nocaute2 && !nocaute1) {
      return this.treinador1;
    }
    if (this.hpAntesDoStatus1 > this.hpAntesDoStatus2) {
      return this.treinador1;
    }
    if (this.hpAntesDoStatus2 > this.hpAntesDoStatus1) {
      return this.treinador2;
    }
    return this.treinador1;
  }

  /**
   * Devolve e limpa o texto do que aconteceu no turno.
   *
   * @return relatório acumulado
   */
  public String consumirRelatorio() {
    String texto = this.relatorio.toString();
    this.relatorio.setLength(0);
    return texto;
  }

  /**
   * @return treinador 1
   */
  public Treinador getTreinador1() {
    return this.treinador1;
  }

  /**
   * @return treinador 2
   */
  public Treinador getTreinador2() {
    return this.treinador2;
  }

  /**
   * @return terreno da batalha
   */
  public Terreno getTerreno() {
    return this.terreno;
  }

  /**
   * @return número do turno atual
   */
  public int getNumeroTurno() {
    return this.numeroTurno;
  }

  private void aplicarStatusDoGolpe(Pokesal origem, Pokesal alvo) {
    StatusEfeito anterior = alvo.getStatus();
    if (origem.getTipo() == TipoElemental.FOGO) {
      alvo.aplicarStatus(StatusEfeito.QUEIMADO);
    } else if (origem.getTipo() == TipoElemental.AGUA) {
      alvo.aplicarStatus(StatusEfeito.PARALISADO);
    } else if (origem.getTipo() == TipoElemental.PLANTA) {
      alvo.aplicarStatus(StatusEfeito.ENVENENADO);
    }
    if (alvo.getStatus() != anterior) {
      registrar(alvo.getNome() + " agora esta " + alvo.getStatus() + ".");
    }
  }

  private void aplicarRegenCanteiro(Treinador treinador) {
    if (this.terreno != Terreno.CANTEIRO_CENTRAL) {
      return;
    }
    Pokesal pokesal = treinador.getPokesal();
    if (pokesal.getTipo() != TipoElemental.PLANTA || pokesal.estaNocauteado()) {
      return;
    }
    int regen = (int) Math.round(
        pokesal.getHpMaximo() * ConstantesBatalha.REGEN_PLANTA_CANTEIRO);
    regen = Math.max(ConstantesBatalha.DANO_MINIMO, regen);
    int hpAntes = pokesal.getHpAtual();
    pokesal.curar(regen);
    int curado = pokesal.getHpAtual() - hpAntes;
    if (curado > 0) {
      registrar(pokesal.getNome() + " recuperou " + curado
          + " de HP no Canteiro Central.");
    }
  }

  private void registrar(String linha) {
    this.relatorio.append(linha);
    this.relatorio.append(System.lineSeparator());
  }
}
