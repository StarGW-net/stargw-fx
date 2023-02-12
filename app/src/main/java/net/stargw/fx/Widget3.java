package net.stargw.fx;


public class Widget3 extends WidgetProvider {

    @Override
    public int getView() {
        Global.Log("Widget Layout 3 (" + R.layout.widget3_layout + ")",3);
        return R.layout.widget3_layout;
    }

    @Override
    public int getWidgetLayout() {
        return 3;
    }
}
