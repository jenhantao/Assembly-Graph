/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modifiedsds;

/**
 *
 * @author vvasilev
 */
public class BasicPart {
    private String _name;
    private int _length;
    private String _type;

    public BasicPart() {

    }

    public BasicPart(String name) {
        _name = name;
    }

    public BasicPart(String name, int length) {
        _name = name;
        _length = length;
    }

    public BasicPart(String name, int length, String type) {
        _name = name;
        _length = length;
        _type = type;
    }

    public int getLength() {
        return _length;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    public void setLength(int length) {
        _length = length;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setType(String type) {
        _type = type;
    }
}
