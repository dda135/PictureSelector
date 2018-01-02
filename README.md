# PictureSelector
![](https://github.com/dda135/PictureSelector/blob/master/readme/p1.jpg)  
![](https://github.com/dda135/PictureSelector/blob/master/readme/p2.jpg)  
![](https://github.com/dda135/PictureSelector/blob/master/readme/p3.jpg)  

usage
-------
```
compile 'com.fanjinho:picture-selector:1.0.1'
```

```Java
//3 selectCount
//1 requestCode
PictureSelectorActivity.start(this,3,1);
```

```Java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:
                    //requestCode
                    ArrayList<String> result = (ArrayList<String>) data.getSerializableExtra(PictureSelectorActivity.EXTRA_SELECT_PICTURES);
                    //img uri,common is content://...
                    for(String temp:result){
                        Log.i(getClass().getSimpleName(),temp);
                    }
                    break;
                default:
                    break;
            }
        }
    }
```
future
-------
add more option to select picture...
