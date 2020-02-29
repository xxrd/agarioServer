import java.util.ArrayList;
import java.util.Collection;

public class ServerMessageBuilder {
    private proto.ServerMessageOuterClass.PlayerList.Builder playerList = proto.ServerMessageOuterClass.PlayerList.newBuilder();
    private proto.ServerMessageOuterClass.FoodList.Builder foodList = proto.ServerMessageOuterClass.FoodList.newBuilder();
    private proto.ServerMessageOuterClass.CurrentPlayer.Builder currentPlayer = proto.ServerMessageOuterClass.CurrentPlayer.newBuilder();
    private proto.ServerMessageOuterClass.ServerMessage.Builder serverMessage = proto.ServerMessageOuterClass.ServerMessage.newBuilder();

    public void setPlayerList(Collection<Player> pl) {
        for(Player player : pl) {
            proto.ServerMessageOuterClass.Player.Builder playerBuilder = proto.ServerMessageOuterClass.Player.newBuilder();
            playerBuilder.setX(player.getX());
            playerBuilder.setY(player.getY());
            playerBuilder.setMass(player.getMass());
            playerList.addPlayer(playerBuilder);
        }
    }

    public void setFoodList(FoodList fl) {
        ArrayList<Food> fList = fl.getList();
        int i = 0;
        for(Food food : fList) {
            proto.ServerMessageOuterClass.Food.Builder foodBuilder = proto.ServerMessageOuterClass.Food.newBuilder();
            foodBuilder.setX(food.getX());
            foodBuilder.setY(food.getY());
            foodBuilder.setMass(food.getMass());
            foodBuilder.setEaten(food.isEaten());
            foodList.addFood(foodBuilder);
            i++;
        }
    }

    public void addPlayer(Player player) {
        proto.ServerMessageOuterClass.Player.Builder playerBuilder = proto.ServerMessageOuterClass.Player.newBuilder();
        playerBuilder.setX(player.getX());
        playerBuilder.setY(player.getY());
        playerBuilder.setMass(player.getMass());
        playerBuilder.setName(player.getName());
        playerBuilder.setId(player.getId());
        playerList.addPlayer(playerBuilder);
    }

    public void setCurrentPlayer(Player cp) {
        currentPlayer.setX(cp.getX());
        currentPlayer.setY(cp.getY());
        currentPlayer.setMass(cp.getMass());
        currentPlayer.setName(cp.getName());
    }

    public void setMessageType(proto.ServerMessageOuterClass.ServerMessage.MessageType type) {
        serverMessage.setMessageType(type);
    }

    public proto.ServerMessageOuterClass.ServerMessage build() {
        if(playerList.isInitialized()) serverMessage.setPlayerList(playerList);
        if(foodList.isInitialized()) serverMessage.setFoodList(foodList);
        if(currentPlayer.isInitialized()) serverMessage.setCurrentPlayer(currentPlayer);

        return serverMessage.build();
    }
}