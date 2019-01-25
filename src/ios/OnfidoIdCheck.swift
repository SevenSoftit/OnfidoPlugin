import Onfido
import Alamofire
import Foundation
import UIKit
class Person {
    var firstName: String?
    var lastName: String?
    let gender = "female"
}

@objc(OnfidoIdCheck) class OnfidoIdCheck : CDVPlugin {


    //variables para el api de onfido.
    private var _token:String = "test_iCPCbZOQv01rBCSZ5xZt65JaqMj_et76"
    private var _Titule_Final:String = ""
    private var _First_Name:String = ""
    private var _Last_Name:String = ""
    private var _Message_Final:String = ""
    private var _Aplicant_Client:String = ""
    //constantes de campos del json de onfido.
    var Key_Token = "Mobile_Token"
    var Key_Titule_Final = "Titule_Final"
    var Key_Message_Final = "Message_Final"
    var Key_Aplicant_Client = "Aplicant_Client"
    var Key_First_Name = "First_name"
    var Key_Last_Name = "Last_name"
    //retornos del api onfido

    func json(from object:Any) -> String? {
        guard let data = try? JSONSerialization.data(withJSONObject: object, options: []) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)
    }

    func readJsonFrom(object: String)-> [String: Any]? {
        let data: Data = object.data(using: .utf8)!
                do {
                    let dict = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]


                        for (key, value) in dict! {
                            // access all key / value pairs in dictionary
                            print("onfido : \(key)")
                           // print(value)
                            let valueStr = self.json(from: value)
                            let dataOnfidoInit: Data = (valueStr as! String).data(using: .utf8)!
                            let dict2 = try JSONSerialization.jsonObject(with: dataOnfidoInit, options: []) as? [String: Any]
                            for (keyOnf, valueOnf) in dict2! {
                             print("keyOnf : \(keyOnf) valor \(valueOnf)")
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
                                else if(keyOnf.contains(self.Key_Aplicant_Client))
                                {
                                    let valueAplication_Client = self.json(from: valueOnf)
                                    let dataAplicant_Client: Data = (valueAplication_Client as! String).data(using: .utf8)!
                                    let dictAplicant_Client = try JSONSerialization.jsonObject(with: dataAplicant_Client, options: []) as? [String: Any]
                                    for (keyAplicant_Client, valueAplicant_Client) in dictAplicant_Client! {

                                        if (keyAplicant_Client.contains(self.Key_First_Name))
                                        {
                                            self._First_Name = "gustavo"//String(describing: valueAplicant_Client)
                                        }
                                        if (keyAplicant_Client.contains(self.Key_Last_Name))
                                        {
                                            self._Last_Name = "Matosas"//String(describing: valueAplicant_Client)

                                        }

                                    }

                                }


                            }
                        }



                   // print(dict)

                } catch let error {
                    print("\(error)")
                }

        return nil

    }

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
                   // completionHandler(nil, ApplicantError.apiError(response["error"] as! [String : Any]))
                    return
                }

               let _ApplicantId = response["id"] as! String

                completionHandler(_ApplicantId, nil)
        }
    }
    private func InvokeCheck( ApplicationID: String) {

        /**
         Note: support for Applicant creation during the Onfido iOS SDK flow is deprecated
         We suggest to create applicants in your backend
         */


        let headers: HTTPHeaders = [
            "Authorization": "Token token=\(_token)",
            "Accept": "application/json"
        ]
        //var report : [String:String]=["name": "document", "name": "facial_similarity","variant": //"video"]

        let jsonObject: [Any]  = [
            [
                "name": "document",
                "variant": "video"

            ]
        ]



        let parameters: [String: Any] = [
            "type": "express",
            "reports": [jsonObject]

        ]

        Alamofire.request(
            "https://api.onfido.com/v2/applicants/"+ApplicationID+"/checks",
            method: .post,
            parameters: parameters,
            encoding: JSONEncoding.default,
            headers: headers).validate().responseJSON { response in

                switch response.result {
                case .success(let value):
                    let jsonr = self.json(from:value)
                    print(jsonr)
                case .failure(let error):
                    print(error)
                }
        }

        //responseCheck: DataResponse<Any>) in

        //guard responseCheck.error == nil else {
        //   return
        //}

        // let response = responseCheck.result.value as! [String: Any]

        // guard response.keys.contains("error") == false else {
        //    return
        //}

        //}



    }
    private func runFlow(forApplicantWithId applicantId: String) {

        let responseHandler: (OnfidoResponse) -> Void = { response in

            if case let OnfidoResponse.error(innerError) = response {

              //  self.showErrorMessage(forError: innerError)

            } else if case OnfidoResponse.success = response {
                self.InvokeCheck( ApplicationID: applicantId);
                let alert = UIAlertController(title: "Success", message: "Success", preferredStyle: .alert)
                let alertAction = UIAlertAction(title: "OK", style: .default, handler: { _ in })
                alert.addAction(alertAction)

                //let vc = ViewController()
                //   var navigationController = alert//UINavigationController(rootViewController: vc)
                // self.presentViewController(navigationController, animated: true, completion: nil
                //  self.present(alert, animated: true)
                self.viewController?.present(alert,animated: true,completion: nil)

            } else if case OnfidoResponse.cancel = response {

                let alert = UIAlertController(title: "Canceled", message: "Canceled by user", preferredStyle: .alert)
                let alertAction = UIAlertAction(title: "OK", style: .default, handler: { _ in })
                alert.addAction(alertAction)
               // self.present(alert, animated: true)
                self.viewController?.present(alert,animated: true,completion: nil)
            }

        }

        let config = try! OnfidoConfig.builder()
            .withToken(_token)
            .withApplicantId(applicantId)
            .withDocumentStep(ofType: .nationalIdentityCard, andCountryCode: "SLV")

            .withFaceStep(ofVariant: .photo)
            .build()

        let onfidoFlow = OnfidoFlow(withConfiguration: config)
            .with(responseHandler: responseHandler)

        do {

            let onfidoRun = try onfidoFlow.run()
            onfidoRun.modalPresentationStyle = .formSheet // to present modally
            self.viewController?.present(onfidoRun,animated: true,completion: nil)
            //self.present(onfidoRun, animated: true, completion: nil)

        } catch let error {

            // cannot execute the flow
            // check CameraPermissions
            //self.showErrorMessage(forError: error)
        }
    }
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
     Key_Token = "Mobile_Token"
     Key_Titule_Final = "Titule_Final"
     Key_Message_Final = "Message_Final"
     Key_Aplicant_Client = "Aplicant_Client"
     Key_First_Name = "first_name"
     Key_Last_Name = "last_name"
     let jsonr = self.json(from:command.arguments[0])
     let jsonq = self.readJsonFrom(object:jsonr!)
     if jsonr != nil {
        self.createClient{ (applicantId, error) in
            guard error == nil else {
               // self.showErrorMessage(forError: error!)
                return
            }
            self.runFlow(forApplicantWithId: applicantId!)
        }
        }
     /* let toastController: UIAlertController =
        UIAlertController(
          title: "",
          message: "hola",
          preferredStyle: .alert

        )

      self.viewController?.present(
        toastController,
        animated: true,
        completion: nil
      )

      DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
        toastController.dismiss(
          animated: true,
          completion: nil
        )
      }

      pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: jsonr
      )


    self.commandDelegate!.send(
      pluginResult,
      callbackId: command.callbackId
    )*/
  }

}
