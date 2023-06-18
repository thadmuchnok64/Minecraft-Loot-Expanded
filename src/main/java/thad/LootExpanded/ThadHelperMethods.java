package thad.LootExpanded;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ThadHelperMethods {
    

    // Inventory Managing
    public static ItemStack FindStackInInventory(Inventory inventory, Item itemid){
                    for(int i = 0; i < inventory.size(); ++i) {
               ItemStack itemStack2 = inventory.getStack(i);
               if (itemStack2.getItem() == itemid)
               {
                  return itemStack2;
               }
            }
            return null;
    }

    public boolean ConsumeIfFoundInInventory(Inventory inventory,Item itemid){
        ItemStack itemStack = FindStackInInventory(inventory, itemid);
        if(itemStack != null && itemStack.getCount()>0){
        itemStack.decrement(1);
        return true;
        }
        return false;
    }
}
