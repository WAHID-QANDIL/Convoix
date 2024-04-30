package com.example.convoix.Dialogs

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CropRotate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Composable
fun StoryPreview(uri: Uri?, hideDialog:()->Unit, upload:(Uri?)->Unit) {
    var croppedUri: Uri? by remember {
        mutableStateOf(null)
    }
    val cropImage = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedUri = result.uriContent
        } else {
            val exception = result.error
        }
    }
    Dialog(onDismissRequest = hideDialog,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    )       {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))) {
        }
        var scale by remember {
            mutableStateOf(1f)
        }
        var offset by remember {
            mutableStateOf(Offset.Zero)
        }
        val brush = Brush.linearGradient(listOf(
            Color(0xFF238CDD),
            Color(0xFF1952C4)
        ))
        val brush2 = Brush.linearGradient(listOf(
            Color(0xFFA02424),
            Color(0xFFC43B56)
        ))
        BoxWithConstraints {
            val state = rememberTransformableState{ zoomChange, panChange, rotationChange ->
                scale = (scale*zoomChange).coerceIn(1f,5f)
                val extWidth = (scale - 1)*constraints.maxWidth
                val extHeight = (scale -1) * constraints.maxHeight
                val maxX = extWidth/2
                val maxY = extHeight/2
                offset = Offset(
                    x=(offset.x + scale * panChange.x).coerceIn(-maxX,maxX),
                    y=(offset.y + scale * panChange.y).coerceIn(-maxY,maxY)
                )
            }
            Column(modifier = Modifier
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = if(croppedUri!=null) croppedUri else uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .transformable(state)
                )
                Row(modifier = Modifier
                    .fillMaxWidth(0.9f).padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = hideDialog, modifier = Modifier.background(brush2, CircleShape), colors = ButtonDefaults.buttonColors(
                        Color.Transparent) ) {
                        Text(text = "Cancel", color = Color.White)
                    }
                    Button(onClick = { upload(croppedUri) }, modifier = Modifier.background(brush, CircleShape), colors = ButtonDefaults.buttonColors(
                        Color.Transparent) ) {
                        Text(text = "Upload", color = Color.White)
                    }
                }
            }
            IconButton(modifier = Modifier.align(Alignment.TopEnd)
                .padding(10.dp),
                onClick = {
                    val cropOptions = CropImageContractOptions(uri, CropImageOptions(activityBackgroundColor = Color(0xFF000000).toArgb()))
                    cropImage.launch(cropOptions)
                }) {
                Icon(imageVector = Icons.Rounded.CropRotate, contentDescription = null)
            }
        }
    }
}