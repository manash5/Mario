    package components;

    import org.joml.Vector2f;
    import util.AssetPool;
    import engine.Window;

    // this class is used to generate coin after a block is collided by a player

    public class BlockCoin extends Component{
        private Vector2f topY;
        private float coinSpeed = 1.4f;

        // Sets the highest point the coin will go at
        @Override
        public void start(){
            topY = new Vector2f(this.gameObject.transform.position.y).add(0, 0.5f);
            AssetPool.getSound("assets/sounds/coin.ogg").play();
            Window.getScene().incrementCoinCounter();
        }

        // makes the coin go up to the highest point and after it is reached that point, it destroys
        @Override
        public void update(float dt){
            if (this.gameObject.transform.position.y< topY.y){
                this.gameObject.transform.position.y += dt * coinSpeed;
                this.gameObject.transform.scale.x -= (0.5 * dt)% -1.0f; // this will make the coin look like it is flipping
            } else {
                gameObject.destroy();
            }
        }
    }
