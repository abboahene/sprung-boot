package org.test.partB;

import org.framework.annotations.Qualifier;
import org.framework.annotations.Service;

@Qualifier(name="testQualifier")
@Service
public class TestQualifier {
    public void testQualifier(){
        System.out.println("Testing Qualifier...");
    }
}
