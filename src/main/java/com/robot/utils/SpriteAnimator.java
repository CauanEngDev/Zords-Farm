package com.robot.utils;

import com.robot.enums.AnimationState;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Componente que gerencia a animação de um ImageView usando spritesheets.
 * Ele usa um AnimationTimer interno para tocar os frames.
 */
public class SpriteAnimator {
    private final ImageView targetImageView;
    private final Map<AnimationState, Image> animations = new HashMap<>();

    private AnimationTimer animationTimer;
    private Object currentState; // O estado atual da animação (IDLE, WALKING, WORKING)

    // Dimensões do Spritesheet
    private final int cols = 6;
    private final int totalFrames = 36;
    private int currentFrame = 0;
    private long lastUpdate = 0;
    private final long frameDuration = 83; // Velocidade de 12 FPS (1000ms / 12)

    // Variáveis de Gatilho (para a lógica de criação)
    private Runnable onFrameAction = null;
    private int targetFrame = -1;
    private Runnable onFisnishFrame = null;
    private boolean isPlayingOnce = false; // Flag para animações de "um só toque"

    public SpriteAnimator(ImageView target) {
        this.targetImageView = target;
        initializeTimer();
    }

    /**
     * Adiciona um novo spritesheet ao catálogo do animador.
     * @param stateKey O estado (IDLE, WALKING, etc.).
     * @param spriteSheet O arquivo PNG/JPG com os frames.
     */
    public void addAnimation(AnimationState stateKey, Image spriteSheet) {
        animations.put(stateKey, spriteSheet);
    }

    /**
     * Inicia a animação para o estado fornecido (loop infinito).
     * @param stateKey O estado a ser reproduzido.
     */
    public void play(AnimationState stateKey) {
        if (currentState == stateKey) return;

        currentState = stateKey;
        currentFrame = 0;

        // O "Porquê": Garante que animações de loop não tenham o flag 'one-shot' ativo.
        if (stateKey == AnimationState.IDLE || stateKey == AnimationState.WALKING)
            this.isPlayingOnce = false;

        Image sheet = animations.get(stateKey);
        if (sheet != null) {
            targetImageView.setImage(sheet);

            // Calcula o tamanho de um único frame
            int frameW = (int) (sheet.getWidth() / cols);
            int frameH = (int) (sheet.getHeight() / (totalFrames / cols));

            // Aplica o Viewport inicial (Frame 0)
            targetImageView.setViewport(new Rectangle2D(0, 0, frameW, frameH));
            animationTimer.start();
        }
    }

    /**
     * Prepara e inicia a lógica do loop de 60 FPS (o Game Loop interno do componente).
     */
    public void initializeTimer() {
        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentState == null || !animations.containsKey(currentState)) return;

                long nowMs = now / 1_000_000;

                // 1. Lógica de Tempo: Checa se o tempo passou (12 FPS)
                if (nowMs - lastUpdate < frameDuration) return;
                lastUpdate = nowMs; // Atualiza o tempo para o próximo frame

                // 2. GATILHO INTERMEDIÁRIO (Ex: Frame 18 da criação)
                if (currentFrame == targetFrame && onFrameAction != null) {
                    onFrameAction.run();
                    onFrameAction = null; // Reseta o gatilho após o disparo
                }

                // 3. AVANÇO DE FRAME
                currentFrame = (currentFrame + 1) % totalFrames;

                // 4. CÁLCULO DE RECORTE (Viewport)
                Image currentSheet = animations.get(currentState);

                int frameW = (int) (currentSheet.getWidth() / cols);
                int frameH = (int) (currentSheet.getHeight() / (totalFrames / cols));

                int col = currentFrame % cols;
                int row = currentFrame / cols;

                // Aplica o novo corte
                targetImageView.setViewport(new Rectangle2D(col * frameW, row * frameH, frameW, frameH));

                // 5. CHECAGEM DE FIM (Lógica One-Shot)
                if (currentFrame >= totalFrames - 1) { // Último frame atingido (35)

                    // O "Porquê": Se é uma animação de um toque (Creation)
                    // E se há uma ação para rodar (limpeza/volta para IDLE)
                    if (isPlayingOnce && onFisnishFrame != null) {
                        onFisnishFrame.run(); // Roda a ação final
                        onFisnishFrame = null;
                        isPlayingOnce = false;
                        // Nota: O timer não é parado aqui, pois showIdleAnimation() fará isso,
                        // mas no nosso modelo atual, a ação final DEVE parar o loop ou ele continua.
                        // Como a lógica do GameApp garante que o showIdleAnimation()
                        // (que toca o loop IDLE) seja chamado, não paramos o timer aqui.
                        return;
                    }

                    // 6. Se for um loop (IDLE/WALKING), reseta o contador
                    if (!isPlayingOnce) currentFrame = 0;
                }
            }
        };
    }

    /**
     * Define uma ação a ser executada em um frame específico (Ex: Spawning).
     * @param frameIndex O índice do frame alvo (0-35).
     * @param action A função Runnable a ser executada.
     */
    public void setActionOnFrame(int frameIndex, Runnable action) {
        this.targetFrame = frameIndex;
        this.onFrameAction = action;
    }

    /**
     * Define uma ação a ser executada após o último frame ser exibido (Limpeza).
     * @param action A função Runnable a ser executada.
     */
    public void setActionOnFinish(Runnable action) {
        this.onFisnishFrame = action;
    }

    /**
     * Prepara a animação para rodar apenas uma vez (One-Shot).
     * @param stateKey O estado a ser reproduzido (Ex: WORKING/CREATION).
     */
    public void playOneShot(AnimationState stateKey) {
        this.isPlayingOnce = true;
        play(stateKey);
    }
}