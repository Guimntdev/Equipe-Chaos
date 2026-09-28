package pokesal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pokesal.batalha.Batalha;
import pokesal.batalha.CalculadoraDano;
import pokesal.batalha.CatalogoInicial;
import pokesal.modelo.AcaoTurno;
import pokesal.modelo.Pokesal;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoGolpe;
import pokesal.modelo.TipoItem;
import pokesal.modelo.Treinador;

/**
 * Testes dos três requisitos autorais da equipe.
 */
class RequisitosAutoraisTest {

  @Test
  void testComboGolpesIguaisAumentaDano() {
    CalculadoraDano calc = new CalculadoraDano();
    Pokesal charSal = CatalogoInicial.criar("CharSal");
    Pokesal bulbaSal = CatalogoInicial.criar("BulbaSal");
    int normal = calc.calcular(charSal, bulbaSal, Terreno.POCA_CHUVA,
        TipoGolpe.ELEMENTAL, false);
    int combo = calc.calcular(charSal, bulbaSal, Terreno.POCA_CHUVA,
        TipoGolpe.ELEMENTAL, true);
    assertTrue(combo > normal);

    Treinador t1 = new Treinador("Miguel", CatalogoInicial.criar("CharSal"));
    Treinador t2 = new Treinador("Dantton", CatalogoInicial.criar("BulbaSal"));
    Batalha batalha = new Batalha(t1, t2, Terreno.POCA_CHUVA);
    int primeiro = batalha.executarAtaque(t1, t2, TipoGolpe.ELEMENTAL);
    int segundo = batalha.executarAtaque(t1, t2, TipoGolpe.ELEMENTAL);
    assertTrue(segundo > primeiro);
    assertTrue(t1.getPokesal().isCombo(TipoGolpe.ELEMENTAL));
  }

  @Test
  void testComboResetaComOutroGolpeOuItem() {
    Treinador t1 = new Treinador("Miguel", CatalogoInicial.criar("CharSal"));
    Treinador t2 = new Treinador("Dantton", CatalogoInicial.criar("BulbaSal"));
    Batalha batalha = new Batalha(t1, t2, Terreno.POCA_CHUVA);
    batalha.executarAtaque(t1, t2, TipoGolpe.ELEMENTAL);
    batalha.executarAtaque(t1, t2, TipoGolpe.INVESTIDA);
    assertFalse(t1.getPokesal().isCombo(TipoGolpe.ELEMENTAL));

    batalha.executarAtaque(t1, t2, TipoGolpe.ELEMENTAL);
    t1.getPokesal().receberDano(5);
    batalha.executarAcao(t1, t2, AcaoTurno.USAR_ITEM, TipoItem.POTION);
    assertFalse(t1.getPokesal().isCombo(TipoGolpe.ELEMENTAL));
  }

  @Test
  void testLutarAteOFimNaoEmpata() {
    Treinador t1 = new Treinador("Miguel",
        new Pokesal("A", TipoElemental.FOGO, 10, 5, 5, 5));
    Treinador t2 = new Treinador("Dantton",
        new Pokesal("B", TipoElemental.PLANTA, 10, 5, 5, 5));
    Batalha batalha = new Batalha(t1, t2, Terreno.POCA_CHUVA);
    t1.getPokesal().receberDano(9);
    t2.getPokesal().receberDano(9);
    t1.getPokesal().aplicarStatus(StatusEfeito.QUEIMADO);
    t2.getPokesal().aplicarStatus(StatusEfeito.QUEIMADO);
    batalha.finalizarTurno();
    assertTrue(batalha.batalhaEncerrada());
    assertNotNull(batalha.obterVencedor());
    assertEquals(t1, batalha.obterVencedor());
  }

  @Test
  void testSemLimiteDeTurnos() {
    Treinador t1 = new Treinador("Miguel",
        new Pokesal("A", TipoElemental.PLANTA, 80, 8, 40, 40));
    Treinador t2 = new Treinador("Dantton",
        new Pokesal("B", TipoElemental.PLANTA, 80, 8, 40, 40));
    Batalha batalha = new Batalha(t1, t2, Terreno.POCA_CHUVA);
    int turnos = 0;
    while (!batalha.batalhaEncerrada() && turnos < 80) {
      batalha.resolverTurno(AcaoTurno.INVESTIDA, null, AcaoTurno.INVESTIDA,
          null);
      turnos++;
    }
    assertTrue(turnos > 10);
    assertTrue(batalha.batalhaEncerrada());
    assertNotNull(batalha.obterVencedor());
  }

  @Test
  void testStatusNaoAcumulaSubstituiAnterior() {
    Pokesal alvo = CatalogoInicial.criar("SquirtSal");
    alvo.aplicarStatus(StatusEfeito.QUEIMADO);
    assertEquals(StatusEfeito.QUEIMADO, alvo.getStatus());
    alvo.aplicarStatus(StatusEfeito.ENVENENADO);
    assertEquals(StatusEfeito.ENVENENADO, alvo.getStatus());
    assertFalse(alvo.getStatus() == StatusEfeito.QUEIMADO);
    alvo.aplicarStatus(StatusEfeito.PARALISADO);
    assertEquals(StatusEfeito.PARALISADO, alvo.getStatus());
  }
}
