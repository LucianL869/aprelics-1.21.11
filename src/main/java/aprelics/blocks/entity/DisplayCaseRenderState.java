package aprelics.blocks.entity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;

public class DisplayCaseRenderState extends BlockEntityRenderState implements GeoRenderState {
    public ItemStack displayedItem = ItemStack.EMPTY;
    private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();


    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return Map.of();
    }
}