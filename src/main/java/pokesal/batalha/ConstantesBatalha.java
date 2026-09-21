package pokesal.batalha;

/**
 * Constantes nomeadas das regras de batalha.
 *
 * <p>Evita magic numbers espalhados pelo código (dano, vantagem, itens e
 * status).
 */
public final class ConstantesBatalha {

  public static final double SUPER_EFETIVO = 2.0;
  public static final double POUCO_EFETIVO = 0.5;
  public static final double NEUTRO = 1.0;

  public static final double BONUS_FOGO_ASFALTO = 0.15;
  public static final double BONUS_AGUA_POCA = 0.10;
  public static final double REGEN_PLANTA_CANTEIRO = 0.05;

  public static final int LIMITE_ITENS_POR_BATALHA = 2;
  public static final int CURA_POTION = 20;
  public static final int CURA_SUPER_POTION = 50;

  public static final double REDUCAO_ATK_QUEIMADO = 0.50;
  public static final double DANO_QUEIMADO_PERCENT = 0.10;
  public static final double DANO_VENENO_BASE_PERCENT = 0.05;
  public static final double REDUCAO_SPD_PARALISADO = 0.50;

  public static final double BONUS_COMBO = 0.50;

  public static final int DANO_MINIMO = 1;
  public static final int HP_MINIMO = 0;
  public static final int STAT_MINIMO = 1;
  public static final int FATOR_DANO = 10;

  private ConstantesBatalha() {
  }
}
