package components;

import engine.GameObject;
import engine.Prefabs;
import engine.Window;

// this class displays the properties and characteristic  of a questionBlock

public class QuestionBlock extends Block{
    private enum BlockType {
        Coin,
        Powerup,
        Invincibility
    }

    public BlockType blockType = BlockType.Coin;



    // when player hits the block it searches for the case of the block and assign the function as fit
    @Override
    void playerHit(PlayerController playerController) {
        switch(blockType){
            case Coin:
                doCoin(playerController);
                break;
            case Powerup:
                doPowerup(playerController);
                break;
            case Invincibility:
                doInvincibility(playerController);
                break;
        }

        // searches for the stateMachine class and triggers it ot inactive since the block have already been used
        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null){
            stateMachine.trigger("setInactive");
            this.setInactive();
        }
    }

    // gets the coin object and sets it positions and adds it to the scene
    private void doCoin(PlayerController playerController){
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(this.gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }

    // gets the mushroom/flower object whenever this function is called
    private void doPowerup(PlayerController playerController){
        if (playerController.isSmall()){
            spawnMushroom();
        } else {
            spawnFlower();
        }

    }

    private void doInvincibility(PlayerController playerController){
        GameObject star = Prefabs.generateStar();
        star.transform.position.set(gameObject.transform.position);
        star.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(star);
    }

    // gets the mushroom object and sets it positions and adds it to the scene
    private void spawnMushroom(){
        GameObject mushroom = Prefabs.generateMushroom();
        mushroom.transform.position.set(gameObject.transform.position);
        mushroom.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(mushroom);
    }

    // gets the flower object and sets it positions and adds it to the scene
    private void spawnFlower(){
        GameObject flower = Prefabs.generateFlower();
        flower.transform.position.set(gameObject.transform.position);
        flower.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(flower);

    }
}
