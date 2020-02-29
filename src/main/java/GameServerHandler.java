import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import proto.ServerMessageOuterClass;

import java.util.*;

public class GameServerHandler extends SimpleChannelInboundHandler<proto.ClientMessageOuterClass.ClientMessage> {

    private ChannelGroup group;
    private Map<ChannelId, Player> playerMap;
    private FoodList foodList;

    public GameServerHandler(ChannelGroup group, Map<ChannelId, Player> playerMap, FoodList foodList) {
        this.foodList = foodList;
        this.group = group;
        this.playerMap = playerMap;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        playerMap.remove(ctx.channel().id());
    }

    private int generateId() {
        int max = 0;
        for (Map.Entry<ChannelId, Player> entry : playerMap.entrySet()) {
            Player v = entry.getValue();
            if(v.getId() > max) max = v.getId();
        }
        return max+1;
    }

    private void firstConnection(ChannelHandlerContext ctx, proto.ClientMessageOuterClass.ClientMessage msg) {
        Player currentPlayer = new Player();
        currentPlayer.setId(generateId());
        if(msg.getClientInformation().hasWindowCenterPosition())
            currentPlayer.setWindowCenter(msg.getClientInformation().getWindowCenterPosition().getX(), msg.getClientInformation().getWindowCenterPosition().getY());
        if(msg.hasPlayerName()) currentPlayer.setName(msg.getPlayerName());
        playerMap.put(ctx.channel().id(), currentPlayer);
        group.add(ctx.channel());

        ServerMessageBuilder smBuilder = new ServerMessageBuilder();

        for (Map.Entry<ChannelId, Player> entry : playerMap.entrySet()) {
            ChannelId k = entry.getKey();
            Player v = entry.getValue();

            if(v.equals(currentPlayer)) continue;
            smBuilder.addPlayer(v);
        }

        smBuilder.setCurrentPlayer(currentPlayer);
        smBuilder.setFoodList(foodList);
        smBuilder.setMessageType(proto.ServerMessageOuterClass.ServerMessage.MessageType.gameInfo);

        ctx.writeAndFlush(smBuilder.build());
    }

    private void updateGame(ChannelHandlerContext ctx, proto.ClientMessageOuterClass.ClientMessage msg) {
        Player currentPlayer = playerMap.get(ctx.channel().id());
        if(currentPlayer == null) return;

        currentPlayer.update(msg);

        for (Map.Entry<ChannelId, Player> entry : playerMap.entrySet()) {
            ChannelId k = entry.getKey();
            Player v = entry.getValue();

            if(v.equals(currentPlayer)) continue;
            if(!currentPlayer.intersect(v)) { continue; }
            if(currentPlayer.getMass() == v.getMass()) { continue; }

            ServerMessageBuilder smBuilderDisconnect = new ServerMessageBuilder();
            smBuilderDisconnect.setMessageType(proto.ServerMessageOuterClass.ServerMessage.MessageType.disconnect);

            if(currentPlayer.getMass() < v.getMass()) {
                v.changeMass(v.getMass() / CONSTANT.MASS_DIVIDER);
                ctx.writeAndFlush(smBuilderDisconnect.build());
                try {
                    ctx.close().await();
                } catch(InterruptedException e) {}
                playerMap.remove(ctx.channel().id());
                return;
            }
        }

        currentPlayer.eatFood(foodList);
        foodList.topUp(CONSTANT.MIN_FOOD_LIST_SIZE, CONSTANT.MAX_FOOD_LIST_SIZE);

        ServerMessageBuilder smBuilder = new ServerMessageBuilder();

        for (Map.Entry<ChannelId, Player> entry : playerMap.entrySet()) {
            ChannelId k = entry.getKey();
            Player v = entry.getValue();
            if(v.equals(currentPlayer)) continue;

            smBuilder.addPlayer(v);
        }
        smBuilder.setCurrentPlayer(currentPlayer);
        smBuilder.setFoodList(foodList);

        smBuilder.setMessageType(proto.ServerMessageOuterClass.ServerMessage.MessageType.gameInfo);
        ctx.writeAndFlush(smBuilder.build());
    }

    private ServerMessageBuilder getServerMessageBuilder(Player currentPlayer, Map<ChannelId, Player> playerMap) {
        ServerMessageBuilder smBuilder = new ServerMessageBuilder();

        for (Map.Entry<ChannelId, Player> entry : playerMap.entrySet()) {
            ChannelId k = entry.getKey();
            Player v = entry.getValue();
            if(v.equals(currentPlayer)) continue;

            smBuilder.addPlayer(v);
        }
        smBuilder.setCurrentPlayer(currentPlayer);
        smBuilder.setFoodList(foodList);
        return smBuilder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, proto.ClientMessageOuterClass.ClientMessage msg) throws Exception {
        switch(msg.getMessageType()) {
            case playerInfo:
                updateGame(ctx, msg);
                break;
            case firstConnect:
                firstConnection(ctx, msg);
                break;
            case disconnect:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
