/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package helper;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author vvasilev
 */
public class StringList extends ArrayList<String> {

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public StringList subList(int start, int end) {
        StringList list = new StringList();
        for (int index = start; index < end; index++) {
            list.add(this.get(index));
        }
        return list;
    }

    public String toString() {
        return StringUtils.join(this, Constants.PART_SEPARATOR);
    }

}
