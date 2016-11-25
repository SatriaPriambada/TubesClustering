#include <bits/stdc++.h>

using namespace std;

string buf;

int main() {
  bool done = 0;
  while (getline(cin, buf)) {
    if (done) {
      bool sip = 0;
      for (int i = 0; i < buf.size(); i++) {
        if (sip) printf("%c", buf[i]);
        if (buf[i] == ',') sip = 1;
      }
      printf("\n");
    } else {
      cout << buf << endl;
    }
    if (buf.substr(0, 5) == "@data") done = 1;
  }

  return 0;
}
