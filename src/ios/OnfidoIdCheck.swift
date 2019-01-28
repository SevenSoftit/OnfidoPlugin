import Onfido
import Alamofire
import Foundation
import UIKit
class OnResult : NSObject{
    public var Error: Bool?=false
    public var Cancel: Bool?=false
    public var Id: String? = ""
    public var ErrorDescription: String? = ""
    func getValues() -> [String: AnyObject] {
        return ["error": self.Error as AnyObject,
                "cancel": self.Cancel as AnyObject,
                "id": self.Id as AnyObject,
                "errordescription": self.ErrorDescription as AnyObject]
    }
}
//Clase proxy para acceder al sdk de onfido.
//aalpizar
//Release 00
//28/2/2019
@objc(OnfidoIdCheck) class OnfidoIdCheck : CDVPlugin {
    //variables para el api de onfido.
    private var _token:String = "" //almacena el token.
    private var _Titule_Final:String = "" // sin uso
    private var _First_Name:String = "" // almacena el nombre del cliente
    private var _Last_Name:String = "" // almacena el apellido del cliente
    private var _Message_Final:String = "" // sin uso
    private var _Aplicant_Client:String = "" // almacena el id del cliente asignado por onfido.
    var _ResultFlow: OnResult = OnResult()
    //constantes de campos del json de onfido.
    var Key_Token = "Mobile_Token"
    var Key_Titule_Final = "Titule_Final"
    var Key_Message_Final = "Message_Final"
    var Key_Aplicant_Client = "Aplicant_Client"
    var Key_First_Name = "First_name"
    var Key_Last_Name = "Last_name"
    var Key_Aplicant_Check = "Aplicant_Check"
    var Key_Reports = "reports"
    var Key_Type = "type"
    var Key_ContryCode: String = "SLV" // Codigo de pais
    var Result: Bool = false
    //retornos del api onfido

    func json(from object:Any) -> String? {
        guard let data = try? JSONSerialization.data(withJSONObject: object, options: []) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)
    }
    //Metodo para leer el archivo json que se envia.
    func readJsonFrom(object: String)-> [String: Any]? {
        let data: Data = object.data(using: .utf8)!
        do {
            let dict = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            for (key, value) in dict! {
                let valueStr = self.json(from: value)
                let dataOnfidoInit: Data = (valueStr as! String).data(using: .utf8)!
                let dict2 = try JSONSerialization.jsonObject(with: dataOnfidoInit, options: []) as? [String: Any]
                for (keyOnf, valueOnf) in dict2! {
                    if (keyOnf.contains(self.Key_Token))
                    {
                        self._token = String(describing: valueOnf)
                    }
                    else if(keyOnf.contains(self.Key_Titule_Final))
                    {
                        self._Titule_Final = String(describing: valueOnf)
                    }
                    else if(keyOnf.contains(self.Key_Message_Final))
                    {
                        self._Message_Final = String(describing: valueOnf)
                    }
                    else if(keyOnf.contains(self.Key_Aplicant_Check))
                    {
                        let valueAplication_Check = self.json(from: valueOnf)
                        let dataAplicant_Check: Data = (valueAplication_Check as! String).data(using: .utf8)!
                        let dictAplicant_Check = try JSONSerialization.jsonObject(with: dataAplicant_Check, options: []) as? [String: Any]
                        for (keyAplicant_Report, valueAplicant_Report) in dictAplicant_Check! {

                            if (keyAplicant_Report.contains("reports"))
                            {
                                let value_Report = self.json(from: valueAplicant_Report)
                                let dataAplicant_Report: Data = (value_Report as! String).data(using: .utf8)!
                            }
                        }
                    }
                    else if(keyOnf.contains(self.Key_Aplicant_Client))
                    {
                        let valueAplication_Client = self.json(from: valueOnf)
                        let dataAplicant_Client: Data = (valueAplication_Client as! String).data(using: .utf8)!
                        let dictAplicant_Client = try JSONSerialization.jsonObject(with: dataAplicant_Client, options: []) as? [String: Any]
                        for (keyAplicant_Client, valueAplicant_Client) in dictAplicant_Client! {
                            if (keyAplicant_Client.contains(self.Key_First_Name))
                            {
                                self._First_Name = String(describing: valueAplicant_Client)
                            }
                            if (keyAplicant_Client.contains(self.Key_Last_Name))
                            {
                                self._Last_Name = String(describing: valueAplicant_Client)
                            }
                        }
                    }
                }
            }
        } catch let error {

        }
        return nil
    }
    // metodo para crear el cliente.
    private func createClient(_ completionHandler: @escaping (String?, Error?) -> Void) {
        let applicant: Parameters = [
            "first_name": self._First_Name,
            "last_name": self._Last_Name
        ]
        let headers: HTTPHeaders = [
            "Authorization": "Token token=\(self._token)",
            "Accept": "application/json"
        ]
        Alamofire.request(
            "https://api.onfido.com/v2/applicants",
            method: .post,
            parameters: applicant,
            encoding: JSONEncoding.default,
            headers: headers).responseJSON { (response: DataResponse<Any>) in
                guard response.error == nil else {
                    completionHandler(nil, response.error)
                    return
                }
                let response = response.result.value as! [String: Any]
                guard response.keys.contains("error") == false else {
                    //implementar en caso de error.
                    // completionHandler(nil, ApplicantError.apiError(response["error"] as! [String : Any]
                    return
                }
                // En caso de exito.
                let _ApplicantId = response["id"] as! String
                completionHandler(_ApplicantId, nil)
        }
    }

    // Metodo que invoca la ejecucion de los flujos de onfido.
    private func runFlow(_ completionHandler: @escaping (Error?) -> Void){
        let responseHandler: (OnfidoResponse) -> Void = { response in

            if case let OnfidoResponse.success(results) = response {
                self._ResultFlow.Error = false;
                completionHandler(nil)
            } else if case let OnfidoResponse.error(innererror) = response {
                self._ResultFlow.Error = true
                switch innererror {
                case OnfidoFlowError.cameraPermission:
                    self._ResultFlow.ErrorDescription = "cameraPermission";
                case OnfidoFlowError.failedToWriteToDisk:
                    self._ResultFlow.ErrorDescription = "spaceError";
                case OnfidoFlowError.microphonePermission:
                    self._ResultFlow.ErrorDescription = "microphonePermission"
                case OnfidoFlowError.upload(let OnfidoApiError):
                    self._ResultFlow.ErrorDescription = "errorUpload"
                case OnfidoFlowError.exception(withError: let error, withMessage: let message):
                    self._ResultFlow.ErrorDescription = "Error"
                default: break
                }
                completionHandler(nil)
            } else if case OnfidoResponse.cancel = response {
                self._ResultFlow.Error  = false
                self._ResultFlow.Cancel = false
                completionHandler(nil)
            }
        }
        let appearance = Appearance(
            primaryColor: UIColor.red ,
            primaryTitleColor: UIColor.green,
            primaryBackgroundPressedColor: UIColor.yellow,
            secondaryBackgroundPressedColor: UIColor.purple
           )


        let config = try! OnfidoConfig.builder()
            .withToken(_token)
            .withApplicantId(self._Aplicant_Client)
            .withDocumentStep(ofType: .nationalIdentityCard, andCountryCode: Key_ContryCode)
            .withFaceStep(ofVariant: .video)
            .withCustomLocalization(andTableName:"Localizable")
            .withAppearance(appearance)
            .build()

        let onfidoFlow = OnfidoFlow(withConfiguration: config)
            .with(responseHandler: responseHandler)

        do {

            let onfidoRun = try onfidoFlow.run()
            onfidoRun.modalPresentationStyle = .formSheet // to present modally
            self.viewController?.present(onfidoRun,animated: true,completion: nil)

        } catch let error {

        }
    }
    //METODO GENERICO PARA MOSTRAR MENSAJES DE ERROR NATIVO.
    private func showErrorMessage(forError error: Error) {

        let alert = UIAlertController(title: "Errored", message: "Onfido SDK Errored \(error)", preferredStyle: .alert)
        let alertAction = UIAlertAction(title: "OK", style: .default, handler: { _ in })
        alert.addAction(alertAction)
        //self.present(alert, animated: true)
        self.viewController?.present(alert,animated: true,completion: nil)
    }

    enum ApplicantError: Error {
        case apiError([String:Any])
    }
    @objc(startSdk:)
    func startSdk(command: CDVInvokedUrlCommand) {
        var pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR
        )
        // INICIALIZACION DE VARIABLES DE LAS LLAVES DEL JSON RECIBIDO.
        Key_Token = "Mobile_Token"
        Key_Titule_Final = "Titule_Final"
        Key_Message_Final = "Message_Final"
        Key_Aplicant_Client = "Aplicant_Client"
        Key_First_Name = "first_name"
        Key_Last_Name = "last_name"
        Key_Aplicant_Check = "Aplicant_Check"
        Key_Reports = "reports"
        Key_Type = "type"
        _ResultFlow = OnResult()
        let jsonr = self.json(from:command.arguments[0])

        let jsonq = self.readJsonFrom(object:jsonr!)
        if jsonr != nil {
            self.createClient{ (applicantId, error) in
                guard error == nil else {

                    return
                }
                self._Aplicant_Client = applicantId!
                self.runFlow{   (error ) in

                    guard error == nil else {

                        return
                    }
                    if (self._ResultFlow.Error == false)
                    {
                        self._ResultFlow.Id = applicantId
                        do {
                            let jsonData = try JSONSerialization.data(withJSONObject: self._ResultFlow.getValues(), options: .prettyPrinted)
                            // here "jsonData" is the dictionary encoded in JSON data

                            //  let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
                            // here "decoded" is of type `Any`, decoded from JSON data

                            // you can now cast it with the right type
                            // if let dictFromJSON = decoded as? [String:String] {
                            // use dictFromJSON
                            //}
                            let theJSONText = String(data: jsonData,
                                                     encoding: .ascii)
                            pluginResult = CDVPluginResult(
                                status: CDVCommandStatus_OK,
                                messageAs: theJSONText
                            )

                        } catch {
                            print(error.localizedDescription)
                        }
                    }else
                    {
                        self._ResultFlow.Id = applicantId
                        var oJsonResult = self.json(from: self._ResultFlow)
                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_ERROR,
                            messageAs: ""
                        )
                    }
                    self.commandDelegate!.send(
                        pluginResult,
                        callbackId: command.callbackId
                    )
                }
            }
        }
    }
}
