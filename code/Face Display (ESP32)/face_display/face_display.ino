#include <SPI.h>
#include <SdFat.h>
#include <Adafruit_GFX.h>
#include <MCUFRIEND_kbv.h>
#include <ESP32Servo.h>
#include <HardwareSerial.h>

#include "warmupimage.h"

#define SD_CS     5

// Bitmap image configuration
#define BMPIMAGEOFFSET  54
#define BUFFPIXEL       20
#define BITMAPDEPTH     24
#define BITMAPWIDTH     320
#define BITMAPHEIGHT    240

#define HEIGHTOFFSET    31
#define CUTOFFEDHEIGHT    213

#define LEFT_EAR 22
#define RIGHT_EAR 21

void Show_Warmup_Animation();
void Read_And_Update_Bitmap(char *nm, int x, int y, bool draw_all);
bool Check_Bitmap_File_Availablity(File bmp_file);
void Show_Error(uint8_t error);

SdFat sd;
uint16_t **color_bitmap;
MCUFRIEND_kbv tft_lcd;

int lcd_width;
int lcd_height;

Servo left_ear;
Servo right_ear;

void setup()
{
  Serial.begin(115200);

  // Allocate buffer for bitmap image
  color_bitmap = (uint16_t**)malloc(sizeof(uint16_t*) * (CUTOFFEDHEIGHT - HEIGHTOFFSET));
  for (int i=0 ; i< CUTOFFEDHEIGHT - HEIGHTOFFSET ; i++)
    color_bitmap[i] = (uint16_t*)malloc(sizeof(uint16_t*) * BITMAPWIDTH);

  // Set LCD deisplay configuration
  uint16_t ID = tft_lcd.readID();
  tft_lcd.begin(ID);
  tft_lcd.setRotation(3);
  Begin_SD();

  lcd_width = tft_lcd.width();
  lcd_height = tft_lcd.height();

  // Set the display emotion to "NONE"
  Read_And_Update_Bitmap("NONE.bmp", 0, 0, true);

  // Setup servo motors
  left_ear.attach(LEFT_EAR);
  right_ear.attach(RIGHT_EAR);
  // Initialize motor angles
  int initial_angle = 90;
  left_ear.write(initial_angle);
  right_ear.write(initial_angle);
}

void Begin_SD()
{
  while (!sd.begin(SD_CS, SPI_HALF_SPEED)) 
    delay(1000);
}

void loop()
{
  String command = Serial.readString();

  if (command != "")
  {
    // Check the index of commas in the command
    int left_ear_angle_index = command.indexOf(",");
    int right_ear_angle_index = command.indexOf(",", left_ear_angle_index+1);
    int emotion_index = command.indexOf(",", right_ear_angle_index+1);

    // If there aren’t enough commas, exit the loop
    int err_index = -1;
    if (left_ear_angle_index == err_index || right_ear_angle_index == err_index || emotion_index == err_index) return;

    // Get motor angles from received command
    int left_ear_angle = command.substring(0, left_ear_angle_index).toInt();
    int right_ear_angle = command.substring(left_ear_angle_index+1, right_ear_angle_index).toInt();

    // Convert emotion to the image file name from received command
    String emotion_image_file = command.substring(right_ear_angle_index+1, command.length() - 2) + ".bmp ";
    char file_name[20] = {0};
    emotion_image_file.toCharArray(file_name, emotion_image_file.length());

    // Set the display emotion from file name
    Read_And_Update_Bitmap(file_name, 0, 0, false);
    
    // Set motor angles according to the command
    left_ear.write(left_ear_angle);
    right_ear.write(right_ear_angle);
  }
}

uint16_t Read_2Bytes_From_File(File& file) 
{
  uint16_t result;
  file.read((uint8_t*)&result, sizeof(result));
  return result;
}

uint32_t Read_4Bytes_From_File(File& file) 
{
  uint32_t result;
  file.read((uint8_t*)&result, sizeof(result));
  return result;
}

void Read_And_Update_Bitmap(char *nm, int x, int y, bool draw_all)
{
  // Open and check the image file
  File bmp_file = sd.open(nm);
  if (!Check_Bitmap_File_Availablity(bmp_file))
  {
    bmp_file.close();
    return;
  }

  // Show warm-up animation before changing the display emotion
  Show_Warmup_Animation();

  uint32_t file_offset;
  uint8_t r, g, b;
  uint32_t line_size = BITMAPWIDTH * BITMAPDEPTH / 8;   // width x RGB color
  int pixel_index;
  uint8_t line_data[line_size];
  uint16_t base_image_line[BITMAPWIDTH];
  
  int height_to_draw = draw_all ? BITMAPHEIGHT : CUTOFFEDHEIGHT;

  // Determine the image range to update -> to reduce drawing and improve speed
  int w = BITMAPWIDTH;
  int h = BITMAPHEIGHT;
  if ((x + w) >= lcd_width) w = lcd_width - x;
  if ((y + h) >= lcd_height) h = lcd_height - y;

  // Draw image pixels line by line
  for (int row = 0; row < height_to_draw; row++) 
  { 
    // Read a line of pixels from the image file
    file_offset = BMPIMAGEOFFSET + (BITMAPHEIGHT - 1 - row) * line_size;
    bmp_file.seek(file_offset);
    bmp_file.read(line_data, sizeof(line_data));

    // Convert RGB pixels data from bitmap to displayable color format
    pixel_index = 0;
    bool update_base_image_line = row < HEIGHTOFFSET || draw_all;    
    for (int index_in_line = 0; index_in_line < line_size; )
    {
      // Extract RGB values from bitmap line data
      b = line_data[index_in_line++];
      g = line_data[index_in_line++];
      r = line_data[index_in_line++];
    
      // Convert and store color data
      if (update_base_image_line)
        base_image_line[pixel_index++] = tft_lcd.color565(r, g, b);
      else
        color_bitmap[row - HEIGHTOFFSET][pixel_index++] = tft_lcd.color565(r, g, b);
    }
    
    // Update a line of the display image
    if (update_base_image_line)
      tft_lcd.pushColors(base_image_line, BITMAPWIDTH, row == 0);
  }

  // Update the display image range at once
  if (!draw_all)
    for (int row = 0; row < CUTOFFEDHEIGHT - HEIGHTOFFSET; row++)
      tft_lcd.pushColors(color_bitmap[row], BITMAPWIDTH, false);

  bmp_file.close();
}

bool Check_Bitmap_File_Availablity(File bmp_file)
{
  uint8_t err_flag = 0;

  if (bmp_file == NULL) err_flag = 10;
  else
  {
    uint16_t bmp_id = Read_2Bytes_From_File(bmp_file);
    uint32_t file_size = Read_4Bytes_From_File(bmp_file);
    uint32_t creator = Read_4Bytes_From_File(bmp_file);
    uint32_t bmp_img_offset = Read_4Bytes_From_File(bmp_file);
    uint32_t header_size = Read_4Bytes_From_File(bmp_file);
    uint32_t bmp_width = Read_4Bytes_From_File(bmp_file);
    uint32_t bmp_height = Read_4Bytes_From_File(bmp_file);
    uint16_t planes = Read_2Bytes_From_File(bmp_file);
    uint16_t bmp_depth = Read_2Bytes_From_File(bmp_file);
    uint32_t file_offset = Read_4Bytes_From_File(bmp_file);
    
    if (bmp_id != 0x4D42) err_flag = 2;                 // bad ID
    else if (planes != 1) err_flag = 3;                // too many planes
    else if (file_offset != 0 && file_offset != 3) err_flag = 4;       // format: 0 = uncompressed, 3 = 565
    else if (bmp_depth != BITMAPDEPTH) err_flag = 5;    // not supported bitmap depth
  }
    
  // if (err_flag != 0) Show_Error(err_flag);
  return err_flag == 0;
}

void Show_Warmup_Animation()
{
  for (int row = 0; row < BITMAPHEIGHT; row++)
    tft_lcd.pushColors((uint16_t*)(&warm_up_image_1[row]), BITMAPWIDTH, row == 0);
  for (int row = 0; row < BITMAPHEIGHT; row++)
    tft_lcd.pushColors((uint16_t*)(&warm_up_image_2[row]), BITMAPWIDTH, row == 0);
  for (int row = 0; row < BITMAPHEIGHT; row++)
    tft_lcd.pushColors((uint16_t*)(&warm_up_image_3[row]), BITMAPWIDTH, row == 0);
  for (int row = 0; row < BITMAPHEIGHT; row++)
    tft_lcd.pushColors((uint16_t*)(&warm_up_image_4[row]), BITMAPWIDTH, row == 0);
  for (int row = 0; row < BITMAPHEIGHT; row++)
    tft_lcd.pushColors((uint16_t*)(&warm_up_image_5[row]), BITMAPWIDTH, row == 0);
}

// Debugging function for error check
void Show_Error(uint8_t error)
{
  switch (error) 
  {
    case 1:
      Serial.println("bad position");
      break;
    case 2:
      Serial.println("bad BMP ID");
      break;
    case 3:
      Serial.println("wrong number of planes");
      break;
    case 4:
      Serial.println("unsupported BMP format");
      break;
    case 5:
      Serial.println("bitmap depth is not supported (support only 24)");
      break;
    case 10:
      Serial.println("file not found");
      break;
    default:
      Serial.print("unknown error, ");   Serial.println(error);
      break;
  }
}
