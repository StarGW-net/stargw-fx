package net.stargw.fox;


public class Widget5 extends WidgetProvider {

    @Override
    public int getView() {
        Global.Log("Widget Layout 5 (" + R.layout.widget5_layout + ")",3);
        return R.layout.widget5_layout;
    }

    @Override
    public int getWidgetLayout() {
        return 5;
    }
}
