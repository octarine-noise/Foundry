package exter.foundry.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import exter.foundry.api.FoundryUtils;
import exter.foundry.container.ContainerMetalInfuser;
import exter.foundry.recipes.InfuserSubstance;
import exter.foundry.recipes.SubstanceGuiTexture;
import exter.foundry.recipes.manager.InfuserRecipeManager;
import exter.foundry.tileentity.TileEntityMetalInfuser;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiMetalInfuser extends GuiFoundry
{

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("foundry:textures/gui/infuser.png");

  private static final int TANK_HEIGHT = 47;
  private static final int INPUT_TANK_X = 85;
  private static final int INPUT_TANK_Y = 43;

  private static final int OUTPUT_TANK_X = 134;
  private static final int OUTPUT_TANK_Y = 43;

  private static final int SUBSTANCE_X = 71;
  private static final int SUBSTANCE_Y = 43;
  private static final int SUBSTANCE_HEIGHT = 47;

  private static final int PROGRESS_X = 42;
  private static final int PROGRESS_Y = 59;
  private static final int PROGRESS_WIDTH = 22;
  private static final int PROGRESS_HEIGHT = 15;
  
  private static final int TANK_OVERLAY_X = 176;
  private static final int TANK_OVERLAY_Y = 0;

  private static final int PROGRESS_OVERLAY_X = 176;
  private static final int PROGRESS_OVERLAY_Y = 53;

  private TileEntityMetalInfuser te_infuser;

  public GuiMetalInfuser(TileEntityMetalInfuser inf, IInventory player_inv)
  {
    super(new ContainerMetalInfuser(inf, player_inv));
    allowUserInput = false;
    ySize = 209;
    te_infuser = inf;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
  {
    super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);

    fontRenderer.drawString("Metal Infuser", 5, 6, 0x404040);
    fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
  {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    mc.renderEngine.bindTexture(GUI_TEXTURE);
    int window_x = (width - xSize) / 2;
    int window_y = (height - ySize) / 2;
    drawTexturedModalRect(window_x, window_y, 0, 0, xSize, ySize);

    //Draw progress bar.
    int progress = te_infuser.GetProgress() * PROGRESS_WIDTH / te_infuser.GetExtractTime();
    if(progress > 0)
    {
      drawTexturedModalRect(window_x + PROGRESS_X, window_y + PROGRESS_Y, PROGRESS_OVERLAY_X, PROGRESS_OVERLAY_Y, progress, PROGRESS_HEIGHT);
    }
    
    DisplayTank(window_x, window_y, INPUT_TANK_X, INPUT_TANK_Y, TANK_HEIGHT, TANK_OVERLAY_X, TANK_OVERLAY_Y, te_infuser.GetTank(TileEntityMetalInfuser.TANK_INPUT));
    DisplayTank(window_x, window_y, OUTPUT_TANK_X, OUTPUT_TANK_Y, TANK_HEIGHT, TANK_OVERLAY_X, TANK_OVERLAY_Y, te_infuser.GetTank(TileEntityMetalInfuser.TANK_OUTPUT));
    
    //Draw substance bar.
    InfuserSubstance sub = te_infuser.GetSubstance();
    if(sub != null && sub.amount > 0)
    {
      SubstanceGuiTexture tex = InfuserRecipeManager.instance.GetSubstanceTexture(sub.type);
      mc.renderEngine.bindTexture(tex.texture);
      int height = sub.amount * TANK_HEIGHT / FoundryUtils.INFUSER_SUBSTANCE_AMOUNT_MAX;

      drawTexturedModalRect(window_x + SUBSTANCE_X, window_y + SUBSTANCE_Y + SUBSTANCE_HEIGHT - height, tex.x, tex.y + SUBSTANCE_HEIGHT - height, SubstanceGuiTexture.TEXTURE_WIDTH, height);
      mc.renderEngine.bindTexture(GetGUITexture());
    }
  }

  @Override
  public void drawScreen(int mouse_x, int mouse_y, float par3)
  {
    super.drawScreen(mouse_x, mouse_y, par3);
    
    //Draw tool tips.
    
    if(isPointInRegion(INPUT_TANK_X,INPUT_TANK_Y,16,TANK_HEIGHT,mouse_x,mouse_y))
    {
      DisplayTankTooltip(mouse_x, mouse_y, te_infuser.GetTank(TileEntityMetalInfuser.TANK_INPUT));
    }

    if(isPointInRegion(OUTPUT_TANK_X,OUTPUT_TANK_Y,16,TANK_HEIGHT,mouse_x,mouse_y))
    {
      DisplayTankTooltip(mouse_x, mouse_y, te_infuser.GetTank(TileEntityMetalInfuser.TANK_OUTPUT));
    }

    if(isPointInRegion(SUBSTANCE_X, SUBSTANCE_Y, SubstanceGuiTexture.TEXTURE_WIDTH, SUBSTANCE_HEIGHT, mouse_x, mouse_y))
    {
      List<String> list = new ArrayList<String>();
      InfuserSubstance sub = te_infuser.GetSubstance();
      if(sub != null && sub.amount > 0)
      {
        list.add(StatCollector.translateToLocal("substance." + sub.type));
        list.add(String.valueOf(sub.amount) + " / " + String.valueOf(FoundryUtils.INFUSER_SUBSTANCE_AMOUNT_MAX) + " mL");
      } else
      {
        list.add("0 / " + String.valueOf(FoundryUtils.INFUSER_SUBSTANCE_AMOUNT_MAX) + " mL");
      }
      drawHoveringText(list, mouse_x, mouse_y, fontRenderer);
    }
  }

  @Override
  protected ResourceLocation GetGUITexture()
  {
    return GUI_TEXTURE;
  }
}
