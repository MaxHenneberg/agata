import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpBaseService {

  baseUrl: string;

  constructor(private http: HttpClient, @Inject('BACKEND_API_URL') private apiUrl: string) {
    this.baseUrl = apiUrl;
    console.log('Backend Url: ' + apiUrl);
  }

  get(path: string): Observable<any> {
    return this.http.get(this.baseUrl + '/api' + path);
  }

  post(path: string, body: any): Observable<any> {
    console.log('Post with Path: ' + path);
    console.log('Post with Body: ');
    console.log(body);
    return this.http.post(this.baseUrl + '/api' + path, body);
  }

  patch(path: string, body: any): Observable<any> {
    return this.http.patch(this.baseUrl + '/api' + path, body);
  }
}
