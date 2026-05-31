# Simon

Consegna 2 (*finale*) del progetto di **ESP**. Consiste in un prototipo funzionante del gioco
[Simon](https://en.wikipedia.org/wiki/Simon_(game)).

### Scelte software
- nel caso (B) della consegna (quando la spunta `don't keep activities` è attivata)
    - se l'app è in `comp_turn` (computer mostra la sequenza) e l'utente la mette in background
        - appena si rimette in foreground l'utente può riprendere la partita, e il computer mostra la sequenza dall'inizio
    - se l'app è in `player_turn` (turno del giocatore) e l'utente la mette in background
        - appena si rimette in foreground l'utente può riprendere la partita, e riprende la sequenza da dove si era fermato
    - se l'app è in `paused` (pausa) e l'utente la mette in background
        - appena si rimette in foreground l'utente può riprendere la partita, e il computer mostra la sequenza dall'inizio

### Dispositivi
- Pixel 9 (fisico, dark mode)
    - Android 16
- Pixel 5 (emulatore)
    - Android 17

### Ambiente di sviluppo
- Android Studio Panda 1 (version: 21.0.9)
- neovim (version: 0.11.1)

---

*Lorenzo Panizzolo - 2117022*
