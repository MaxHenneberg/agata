import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpBaseService {

  baseUrl: string;
  basePort: string;

  constructor(private http: HttpClient) {
    this.basePort = '8080';
    this.baseUrl = 'http://localhost';
  }

  y;

  get(path: string): Observable<any> {
    return this.http.get(this.baseUrl + ':' + this.basePort + '/api' + path);
  }

  post(path: string, body: any): Observable<any> {
    console.log('Post with Path: ' + path);
    console.log('Post with Body: ');
    console.log(body);
    return this.http.post(this.baseUrl + ':' + this.basePort + '/api' + path, body);
  }

  patch(path: string, body: any): Observable<any> {
    return this.http.patch(this.baseUrl + ':' + this.basePort + '/api' + path, body);
  }
}
