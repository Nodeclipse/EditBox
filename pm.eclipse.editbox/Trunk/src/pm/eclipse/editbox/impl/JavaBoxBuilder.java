package pm.eclipse.editbox.impl;



public class JavaBoxBuilder extends BoxBuilderImpl {

	protected void addLine(int start, int end, int offset, boolean empty) {
		if (!empty) {
			if (text.charAt(start) == '*'){
				emptyPrevLine = !commentStarts(currentbox.start, currentbox.end);
				if (!emptyPrevLine){
					if (currentbox.offset < offset) {
						offset = currentbox.offset;
						start-= offset - currentbox.offset;
					}
				} else {
				 start--;
				 offset--;
				}
			}else if (emptyPrevLine && isClosingToken(start, end)) {
				emptyPrevLine = false;  //block closing expands block
			}else if (!emptyPrevLine && commentStarts(start, end)){
				emptyPrevLine = true;  // block comment start
			}
			addbox0(start, end, offset);
			emptyPrevLine = commentEnds(start, end);
		} else {
			emptyPrevLine = true;
		}
	}

	private boolean commentStarts(int start, int end){
		return end - start > 1 && text.charAt(start) == '/' && text.charAt(start+1) == '*';
	}
	
	private boolean commentEnds(int start, int end) {
		for(int i = start; i<end; i++)
			if (text.charAt(i)=='*' && text.charAt(i+1)=='/')
				return true;
		return false;
	}

	private boolean isClosingToken(int start, int end) {
		int open = 0;
		int close = 0;
		for (int i = start; i <= end; i++) {
			if (text.charAt(i) == '}') {
				if (open > 0)
					open--;
				else
					close++;
			} else if (text.charAt(i) == '}') {
				open++;
			}
		}
		return close > 0;
	}
}
