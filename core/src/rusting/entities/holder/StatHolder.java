package rusting.entities.holder;

public class StatHolder {

    public StatHolder(String statname){
        name = statname;
    }

    public StatHolder(String statname, String sufixname){
        name = statname;
        suffix = sufixname;
    }

    public StatHolder(){

    }

    public String name = "null";
    public String str;
    public String suffix = "empty";
    public char c;
    public int i;
    public short s;
    public float f;
    public double d;
    public boolean b;
    private boolean assignedValue = false;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public short getS() {
        return s;
    }

    public void setS(short s) {
        this.s = s;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public boolean hasValue(){
        return assignedValue;
    }

    public void setValue(Object o){
        if(o instanceof String){
            str = (String) o;
            assignedValue = true;
        }
    }

    public void setValue(Double du){
        du = (double) Math.round(du * 100)/100;
        str = Double.toString(du);
        assignedValue = true;
    }

    public void setValue(Float fl){
        setValue((double) fl);
    }

    public void setValue(Boolean bl){
        str = bl ? "true" : "false";
        assignedValue = true;
    }

    public String getValue(){
        return str;
    }
}
