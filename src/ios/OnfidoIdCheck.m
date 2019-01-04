/********* OnfidoIdCheck.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

@interface OnfidoIdCheck : CDVPlugin {
// Member variables go here.
}

- (void)coolMethod:(CDVInvokedUrlCommand*)command;
- (void)add:(CDVInvokedUrlCommand*)command;
- (void)multiply:(CDVInvokedUrlCommand*)command;
- (void)substract:(CDVInvokedUrlCommand*)command;
- (void)divide:(CDVInvokedUrlCommand*)command;

    @end

    @implementation OnfidoIdCheck

    - (void)add:(CDVInvokedUrlCommand*)command
{
  CDVPluginResult* pluginResult = nil;

  NSNumber *param1 = [[command.arguments objectAtIndex:0] valueForKey:@"param1"];
NSNumber *param2 = [[command.arguments objectAtIndex:0] valueForKey:@"param2"];

if (param1 >=0 && param2 >= 0)
{

  NSString* total = @(param1 + param2);
pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:total];
}
else
{
  pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid parameters"];
}

[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

- (void)substract:(CDVInvokedUrlCommand*)command
{
  CDVPluginResult* pluginResult = nil;

  NSNumber *param1 = [[command.arguments objectAtIndex:0] valueForKey:@"param1"];
NSNumber *param2 = [[command.arguments objectAtIndex:0] valueForKey:@"param2"];

if (param1 >=0 && param2 >= 0)
{

  NSString* total = @(param1 - param2);
pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:total];
}
else
{
  pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid parameters"];
}

[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

- (void)multiply:(CDVInvokedUrlCommand*)command
{
  CDVPluginResult* pluginResult = nil;

  NSNumber *param1 = [[command.arguments objectAtIndex:0] valueForKey:@"param1"];
NSNumber *param2 = [[command.arguments objectAtIndex:0] valueForKey:@"param2"];

if (param1 >=0 && param2 >= 0)
{

  NSString* total = @(param1 * param2);
pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:total];
}
else
{
  pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid parameters"];
}

[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}
- (void)divide:(CDVInvokedUrlCommand*)command
{
  CDVPluginResult* pluginResult = nil;

  NSNumber *param1 = [[command.arguments objectAtIndex:0] valueForKey:@"param1"];
NSNumber *param2 = [[command.arguments objectAtIndex:0] valueForKey:@"param2"];

if (param1 >=0 && param2 >= 0)
{

  NSString* total = @(param1 / param2);
pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:total];
}
else
{
  pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid parameters"];
}

[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}



@end

