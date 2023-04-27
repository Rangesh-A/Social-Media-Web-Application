#include <iostream>
#include "Model_Authenticator.h"
#include <jni.h>
#include <iads.h>
#include <AdsHlp.h>
#include<atlbase.h>

JNIEXPORT jboolean JNICALL Java_Model_Authenticator_authenticate
(JNIEnv* env, jclass cls, jstring name, jstring pass) {
    jboolean isCopyUsername;
    const char* c_name = env->GetStringUTFChars(name, &isCopyUsername);
    jboolean isCopyPassword;
    const char* c_pass = env->GetStringUTFChars(pass, &isCopyPassword);
       
    jboolean result = false;
    IADsOpenDSObject* pDSO = NULL;
    HRESULT hr = S_OK;
    CoInitialize(NULL);
    IDirectorySearch* pDSSearch = NULL;

    hr = ADsOpenObject(L"LDAP://192.168.10.161:389", L"admin@zohor.com", L"Pubgmobile12", 0, IID_IDirectorySearch, (void**)&pDSSearch);

    if (FAILED(hr)) {
        return false;
    }
    LPWSTR pszAttr[] = {(LPWSTR)L"samAccountName" };
    ADS_SEARCH_HANDLE hSearch;
    DWORD dwCount = sizeof(pszAttr) / sizeof(LPWSTR);


    std::string email = c_name;
    std::string phone = c_name;
    std::string username = c_name;
    std::wstring filter = L"(&(objectCategory=person)(objectClass=user)(|(mail=" + std::wstring(email.begin(), email.end()) + L")(samAccountName=" + std::wstring(username.begin(), username.end()) + L")(telephoneNumber=" + std::wstring(phone.begin(), phone.end()) + L")))";
    LPOLESTR lpFilter = (LPOLESTR)filter.c_str();

    
    HRESULT hResult = pDSSearch->ExecuteSearch(lpFilter, pszAttr, dwCount, &hSearch);
    if (FAILED(hResult))
    {
        pDSSearch->Release();
        result = false;
    }
    LPWSTR    logonName = NULL;
    ADS_SEARCH_COLUMN col;
    if (SUCCEEDED(hResult))
    {
        hResult = pDSSearch->GetNextRow(hSearch);
        if (SUCCEEDED(hResult))
        {
            hResult = pDSSearch->GetColumn(hSearch, CComBSTR("sAMAccountName"), &col);
            if (SUCCEEDED(hResult))
            {
                for (DWORD i = 0; i < col.dwNumValues; i++)
                {
                    logonName = col.pADsValues[i].CaseIgnoreString;
                }
            }
            hResult = pDSSearch->GetNextRow(hSearch);
        }
    }

    int size = WideCharToMultiByte(CP_UTF8, 0, logonName, -1, NULL, 0, NULL, NULL);
    if (size > 0) {
        char* fname = new char[size];
        WideCharToMultiByte(CP_UTF8, 0, logonName, -1, fname, size, NULL, NULL);

        hr = ADsGetObject(L"LDAP:", IID_IADsOpenDSObject, (void**)&pDSO);
        if (SUCCEEDED(hr))
        {
            IDispatch* pDisp;
            hr = pDSO->OpenDSObject(CComBSTR("LDAP://192.168.10.161:389/OU=Zoho,DC=zohor,DC=com"),
                CComBSTR(fname),
                CComBSTR(c_pass),
                ADS_SECURE_AUTHENTICATION,
                &pDisp);
            pDSO->Release();
            if (SUCCEEDED(hr))
            {
                result = true;
            }
            else {
                result = false;
            }
        }
        delete[] fname;
    }
    pDSSearch->Release(); 
    CoUninitialize();
    env->ReleaseStringUTFChars(name, c_name);
    env->ReleaseStringUTFChars(pass, c_pass);
    return result;
}
