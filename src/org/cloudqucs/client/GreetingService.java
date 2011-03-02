
package org.cloudqucs.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService {
  String greetServer(String name) throws IllegalArgumentException ;

}
