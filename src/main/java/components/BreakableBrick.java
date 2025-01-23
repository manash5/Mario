package components;

import util.AssetPool;

// this class represents the breakable brick of the mario gam e

public class BreakableBrick extends Block{
    // here after the player has hit the brick, it checks if the player is small or not, if it is not then it plays an audio and destroys
    // the brick
    @Override
    void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()){
            AssetPool.getSound("assets/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
