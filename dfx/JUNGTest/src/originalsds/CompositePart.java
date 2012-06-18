/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package originalsds;

import java.util.ArrayList;

/**
 *
 * @author vvasilev
 */
public class CompositePart extends ArrayList<BasicPart> {

    private int _length = 0;
    private String _type = "";

    public CompositePart() {
        super();
    }
    
    public CompositePart(String part) {
        super();
        for (int index = 0; index < part.length(); index++) {
            this.add(new BasicPart(part.charAt(index)+ ""));
        }
    }

    public CompositePart(CompositePart left, CompositePart right) {
        super();
        combine(left, right);
    }
    
    @Override
    public boolean equals(Object part2) {
        if (part2 instanceof CompositePart) {
            return this.toString().equals(((CompositePart)part2).toString());
        }
        return false;
    }

    public void addBasicPart(BasicPart part) {
        this.add(part);
    }

    public final void combine(CompositePart left, CompositePart right) {
        for (int index = 0; index < left.size(); index++) {
            this.add(left.get(index));
        }
        for (int index = 0; index < right.size(); index++) {
            this.add(right.get(index));
        }
    }

    public final void combine(ArrayList<CompositePart> list) {
        this.clear();
        for (CompositePart compPart: list) {
            for (BasicPart basicPart: compPart) {
                this.add(basicPart);
            }
        }
    }

    public CompositePart subpart(int start, int end) {
        CompositePart newPart = new CompositePart();
        for (int index = start; index < end; index++) {
            newPart.add(this.get(index));
        }
        return newPart;
    }

    public void copy(CompositePart part) {
        this.clear();
        _type = "";
        _length = 0;
        
        for (int ind = 0; ind < part.size(); ind++) {
            BasicPart bp = new BasicPart(part.get(ind).getName(), part.get(ind).getLength(), part.get(ind).getType());
            this.add(bp);
        }
    }


    public int getLength() {
        _length = 0;
        for (int index = 0; index < this.size(); index++) {
            _length += this.get(index).getLength();
        }
        _length += (this.size() - 1) * 6;
        return _length;
    }

    public String getType() {
        _type = "";
        for (int index = 0; index < this.size(); index++) {
            _type += this.get(index).getType();
            if (index < this.size() - 1) {
                _type += ".";
            }
        }
        return _type;
    }

    @Override
    public String toString() {
        String join = "";
        for (int index = 0; index < this.size(); index++) {
            join += this.get(index).getName();
            if (index < this.size() - 1) {
                join += ".";
            }
        }
        return join;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
