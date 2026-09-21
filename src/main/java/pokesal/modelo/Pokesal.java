package pokesal.modelo;

import pokesal.batalha.ConstantesBatalha;

/**
 * Pokésal de batalha, com atributos base e estado atual (HP, status e combo).
 */
public class Pokesal {

  private final String nome;
  private final TipoElemental tipo;
  private final int hpMaximo;
  private int hpAtual;
  private final int atkBase;
  private final int defBase;
  private final int spdBase;
  private StatusEfeito status;
  private int turnosEnvenenado;
  private TipoGolpe ultimoGolpe;

  /**
   * Cria um Pokésal com os atributos base do torneio.
   *
   * @param nome nome do inicial
   * @param tipo tipo elemental
   * @param hp pontos de vida máximos
   * @param atk ataque base
   * @param def defesa base
   * @param spd velocidade base
   */
  public Pokesal(String nome, TipoElemental tipo, int hp, int atk, int def,
      int spd) {
    if (nome == null || nome.trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do Pokesal e obrigatorio.");
    }
    if (tipo == null) {
      throw new IllegalArgumentException("Tipo elemental e obrigatorio.");
    }
    if (hp < ConstantesBatalha.STAT_MINIMO) {
      throw new IllegalArgumentException("HP deve ser pelo menos 1.");
    }
    if (atk < 0 || def < 0 || spd < 0) {
      throw new IllegalArgumentException(
          "ATK, DEF e SPD nao podem ser negativos.");
    }
    this.nome = nome;
    this.tipo = tipo;
    this.hpMaximo = hp;
    this.hpAtual = hp;
    this.atkBase = atk;
    this.defBase = def;
    this.spdBase = spd;
    this.status = StatusEfeito.NENHUM;
    this.turnosEnvenenado = 0;
    this.ultimoGolpe = null;
  }

  /**
   * Retorna o ataque efetivo, já considerando queimadura.
   *
   * @return ATK usado no cálculo de dano
   */
  public int getAtkEfetivo() {
    int atk = this.atkBase;
    if (this.status == StatusEfeito.QUEIMADO) {
      atk = (int) Math.round(atk * (1.0 - ConstantesBatalha.REDUCAO_ATK_QUEIMADO));
    }
    return Math.max(ConstantesBatalha.STAT_MINIMO, atk);
  }

  /**
   * Retorna a defesa efetiva.
   *
   * @return DEF usada no cálculo de dano
   */
  public int getDefEfetivo() {
    return Math.max(ConstantesBatalha.STAT_MINIMO, this.defBase);
  }

  /**
   * Retorna a velocidade efetiva, já considerando paralisia.
   *
   * @return SPD usada para decidir a iniciativa
   */
  public int getSpdEfetivo() {
    int spd = this.spdBase;
    if (this.status == StatusEfeito.PARALISADO) {
      spd = (int) Math.round(
          spd * (1.0 - ConstantesBatalha.REDUCAO_SPD_PARALISADO));
    }
    return Math.max(ConstantesBatalha.STAT_MINIMO, spd);
  }

  /**
   * Aplica dano, sem deixar o HP ficar negativo.
   *
   * @param dano quantidade de dano recebido
   */
  public void receberDano(int dano) {
    if (dano < 0) {
      return;
    }
    this.hpAtual = Math.max(ConstantesBatalha.HP_MINIMO, this.hpAtual - dano);
  }

  /**
   * Cura HP sem ultrapassar o máximo.
   *
   * @param quantidade pontos de vida restaurados
   */
  public void curar(int quantidade) {
    if (quantidade < 0) {
      return;
    }
    this.hpAtual = Math.min(this.hpMaximo, this.hpAtual + quantidade);
  }

  /**
   * Indica se o Pokésal está nocauteado (HP zerado).
   *
   * @return true se não pode mais agir
   */
  public boolean estaNocauteado() {
    return this.hpAtual <= ConstantesBatalha.HP_MINIMO;
  }

  /**
   * Aplica um status, substituindo o anterior. Um Pokésal só pode ter um
   * efeito por vez.
   *
   * @param novoStatus status que entra em vigor
   */
  public void aplicarStatus(StatusEfeito novoStatus) {
    if (novoStatus == null || novoStatus == StatusEfeito.NENHUM) {
      limparStatus();
      return;
    }
    this.status = novoStatus;
    this.turnosEnvenenado = 0;
  }

  /**
   * Remove qualquer efeito de status.
   */
  public void limparStatus() {
    this.status = StatusEfeito.NENHUM;
    this.turnosEnvenenado = 0;
  }

  /**
   * Aplica o dano de Queimado ou Envenenado no fim do turno.
   */
  public void aplicarEfeitosStatusFimTurno() {
    if (estaNocauteado()) {
      return;
    }
    if (this.status == StatusEfeito.QUEIMADO) {
      int dano = (int) Math.round(
          this.hpMaximo * ConstantesBatalha.DANO_QUEIMADO_PERCENT);
      receberDano(Math.max(ConstantesBatalha.DANO_MINIMO, dano));
    } else if (this.status == StatusEfeito.ENVENENADO) {
      this.turnosEnvenenado++;
      double percentual = ConstantesBatalha.DANO_VENENO_BASE_PERCENT
          * this.turnosEnvenenado;
      int dano = (int) Math.round(this.hpMaximo * percentual);
      receberDano(Math.max(ConstantesBatalha.DANO_MINIMO, dano));
    }
  }

  /**
   * @return nome do Pokésal
   */
  public String getNome() {
    return this.nome;
  }

  /**
   * @return tipo elemental
   */
  public TipoElemental getTipo() {
    return this.tipo;
  }

  /**
   * @return HP máximo
   */
  public int getHpMaximo() {
    return this.hpMaximo;
  }

  /**
   * @return HP atual
   */
  public int getHpAtual() {
    return this.hpAtual;
  }

  /**
   * @return ATK da espécie, sem status
   */
  public int getAtkBase() {
    return this.atkBase;
  }

  /**
   * @return DEF da espécie
   */
  public int getDefBase() {
    return this.defBase;
  }

  /**
   * @return SPD da espécie, sem paralisia
   */
  public int getSpdBase() {
    return this.spdBase;
  }

  /**
   * @return status atual
   */
  public StatusEfeito getStatus() {
    return this.status;
  }

  /**
   * @return quantos ticks de veneno já foram aplicados
   */
  public int getTurnosEnvenenado() {
    return this.turnosEnvenenado;
  }

  /**
   * Diz se o golpe atual fecha combo com o golpe do turno anterior.
   *
   * @param golpeAtual golpe que está sendo usado agora
   * @return true se for o mesmo golpe duas vezes seguidas
   */
  public boolean isCombo(TipoGolpe golpeAtual) {
    if (golpeAtual == null) {
      return false;
    }
    if (this.ultimoGolpe == null) {
      return false;
    }
    if (this.ultimoGolpe == golpeAtual) {
      return true;
    }
    return false;
  }

  /**
   * Guarda o golpe deste turno para o próximo combo.
   *
   * @param golpeAtual golpe que acabou de ser usado
   */
  public void registrarGolpe(TipoGolpe golpeAtual) {
    this.ultimoGolpe = golpeAtual;
  }

  /**
   * Zera o combo (item ou qualquer ação que não seja golpe).
   */
  public void resetarCombo() {
    this.ultimoGolpe = null;
  }
}
