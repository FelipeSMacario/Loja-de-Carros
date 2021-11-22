import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UploadService {

  url : string = "http://localhost:8080/imagens";

  constructor(private httpClient : HttpClient) { }

  upload(files : Set<File>, id : number) {

    const formData = new FormData();
    files.forEach(file => formData.append("file", file, file.name));  
    formData.append("id", id.toString());    

    const request = new HttpRequest("POST", this.url, formData);

    return this.httpClient.request(request);
  }  

  
  
  
}
