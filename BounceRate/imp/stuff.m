% Parameters
path = '';
interval = 60;
threshold = 400;

% Read in all video frames and convert to gray scale.
vidObj = VideoReader(path);
colorVidFrames = zeros(vidObj.Height, vidObj.Width, 3);
grayVidFrames = zeros(vidObj.Height, vidObj.Width);
frame = 1;
while(hasFrame(vidObj))
	colorVidFrames(:, :, :, frame) = readFrame(vidObj);
	grayVidFrames(:, :, frame) = double(rgb2gray(colorVidFrames(:, :, :, frame)));
	frame = frame + 1;
end

% Determine when to start keeping track of time: not yet implemented
start_frame = 1;

% Determine when skin has returned to normal color.
final_frame = start_frame - 1;
current_threshold = Inf;

while(current_threshold > threshold)
	final_frame = final_frame + 1;

	% SSD
	current_threshold = grayVidFrames(:, :, final_frame) - grayVidFrames(:, :, final_frame + 1);
	current_threshold = current_threshold .^ 2;
	current_threshold = sum(current_threshold(:))
end